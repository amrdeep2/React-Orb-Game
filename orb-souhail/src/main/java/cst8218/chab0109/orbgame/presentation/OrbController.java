package cst8218.chab0109.orbgame.presentation;
/*
 * Name : Souhail chabli
 * Student id: 041124852
 * course: CST8218 
 * Lab section: 302
*/

import cst8218.chab0109.orbgame.business.OrbFacade;
import cst8218.chab0109.orbgame.entity.Orb;
import cst8218.chab0109.orbgame.presentation.util.JsfUtil;
import cst8218.chab0109.orbgame.presentation.util.PaginationHelper;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * JSF Controller used to manage CRUD operations for Orb entities.
 * 
 * This controller interacts with:
 *  - OrbFacade (EJB) for database operations
 *  - JSF pages for listing, creating, editing, and deleting orbs
 *  - PaginationHelper for paginated table browsing
 *
 * It stores the selected Orb in session scope and supports navigation between pages.
 */
@Named("orbController")
@SessionScoped
public class OrbController implements Serializable {

    /** Injected EJB that performs JPA operations for Orb entities */
    @Inject
    private OrbFacade orbFacade;

    /** Currently selected Orb used in JSF pages */
    private Orb selected;

    /** Model for paginated table of Orbs */
    private DataModel<Orb> items = null;

    /** Pagination helper to manage next/previous pages */
    private PaginationHelper pagination;

    /** Index of the selected Orb in the paginated list */
    private int selectedItemIndex;

    public OrbController() {
    }

    // -------------------- Constants for JSF Validation --------------------

    /** Exposes Orb.X_MAX to JSF for input validation */
    public int getX_MAX() { return Orb.X_MAX; }

    /** Exposes Orb.Y_MAX to JSF for input validation */
    public int getY_MAX() { return Orb.Y_MAX; }

    /** Exposes Orb.SIZE_MAX to JSF for input validation */
    public int getSIZE_MAX() { return Orb.SIZE_MAX; }

    /** Exposes Orb.MAX_SPEED constant to JSF */
    public int getMAX_SPEED() { return Orb.MAX_SPEED; }

    /** Exposes Orb.INITIAL_SIZE constant */
    public int getINITIAL_SIZE() { return Orb.INITIAL_SIZE; }

    /** Exposes Orb.DECAY_RATE constant */
    public int getDECAY_RATE() { return Orb.DECAY_RATE; }

    // -------------------- CRUD Support Methods --------------------

    /**
     * Returns the currently selected Orb.
     * Creates a new empty Orb if none is selected yet.
     */
    public Orb getSelected() {
        if (selected == null) {
            selected = new Orb();
            selectedItemIndex = -1;
        }
        return selected;
    }

    /**
     * Creates and returns pagination helper for 10 items per page.
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                /** Returns total number of Orb entities in DB */
                @Override
                public int getItemsCount() {
                    return orbFacade.count();
                }

                /** Loads a page of orbs into a DataModel */
                @Override
                public DataModel<Orb> createPageDataModel() {
                    int first = getPageFirstItem();
                    int pageSize = getPageSize();
                    List<Orb> list = orbFacade.findRange(new int[]{first, first + pageSize});
                    return new ListDataModel<>(list);
                }
            };
        }
        return pagination;
    }

    /**
     * Navigates to List.xhtml and reloads model.
     */
    public String prepareList() {
        recreateModel();
        return "List";
    }

    /**
     * Loads View.xhtml for the selected Orb.
     */
    public String prepareView() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        return "View";
    }

    /**
     * Navigates to Create.xhtml and resets selected Orb.
     */
    public String prepareCreate() {
        selected = new Orb();
        selectedItemIndex = -1;
        return "Create";
    }

    /**
     * Saves a new Orb to the database.
     */
    public String create() {
        try {
            orbFacade.create(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle").getString("OrbCreated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(
                e,
                ResourceBundle.getBundle("Bundle").getString("PersistenceErrorOccured")
            );
            return null;
        }
    }

    /**
     * Opens Edit.xhtml for modifying an existing Orb.
     */
    public String prepareEdit() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        return "Edit";
    }

    /**
     * Updates an existing Orb.
     */
    public String update() {
        try {
            orbFacade.edit(selected);
            JsfUtil.addSuccessMessage(
                ResourceBundle.getBundle("/Bundle").getString("OrbUpdated")
            );
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(
                e,
                ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured")
            );
            return null;
        }
    }

    /**
     * Deletes the selected Orb.
     */
    public String destroy() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    /**
     * Deletes the Orb and then either opens View.xhtml or List.xhtml
     * depending on whether items remain.
     */
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();

        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            recreateModel();
            return "List";
        }
    }

    /**
     * Performs the deletion + handles exceptions and success messages.
     */
    private void performDestroy() {
        try {
            Orb toDelete = orbFacade.find(selected.getId());
            if (toDelete != null) {
                orbFacade.remove(toDelete);
            }
            JsfUtil.addSuccessMessage(
                ResourceBundle.getBundle("/Bundle").getString("OrbDeleted")
            );
        } catch (Exception e) {
            JsfUtil.addErrorMessage(
                e,
                ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured")
            );
        }
    }

    /**
     * Updates selected item after deletion or page changes.
     */
    private void updateCurrentItem() {
        int count = orbFacade.count();

        if (selectedItemIndex >= count) {
            selectedItemIndex = count - 1;

            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }

        if (selectedItemIndex >= 0) {
            List<Orb> list = orbFacade.findRange(
                new int[]{selectedItemIndex, selectedItemIndex + 1}
            );
            selected = list.isEmpty() ? null : list.get(0);
        }
    }

    /**
     * Returns the DataModel used for displaying paginated results.
     */
    public DataModel<Orb> getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    /** Clears cached DataModel so next request reloads data */
    private void recreateModel() { items = null; }

    /** Clears pagination helper so next request regenerates it */
    private void recreatePagination() { pagination = null; }

    /** Moves to next page */
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    /** Moves to previous page */
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    /** Used for multi-select components in JSF */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(orbFacade.findAll(), false);
    }

    /** Used for dropdowns (single-select) */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(orbFacade.findAll(), true);
    }

    /**
     * JSF Converter for Orb entities → ensures IDs convert to Orb objects and back.
     */
    @FacesConverter(forClass = Orb.class)
    public static class OrbControllerConverter implements Converter {

        /** Converts string ID → Orb object */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            OrbController controller = (OrbController)
                    facesContext.getApplication().getELResolver()
                    .getValue(facesContext.getELContext(), null, "orbController");
            return controller.orbFacade.find(Long.valueOf(value));
        }

        /** Converts Orb → String ID */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Orb) {
                return ((Orb) object).getId().toString();
            } else {
                throw new IllegalArgumentException("object " + object + " is not an Orb");
            }
        }
    }
}

package cst8218.chab0109.orbgame.presentation;
/*
 * Name : Souhail chabli
 * Student id: 041124852
 * course:CST8218 
 * LAb section:302
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

@Named("orbController")
@SessionScoped
public class OrbController implements Serializable {

    @Inject
    private OrbFacade orbFacade; // EJB injection

    private Orb selected; // currently selected Orb
    private DataModel<Orb> items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public OrbController() {
    }

    // -------------------- Constants for JSF Validation --------------------
    
    /**
     * Exposes X_MAX constant to JSF pages for validation
     */
    public int getX_MAX() {
        return Orb.X_MAX;
    }

    /**
     * Exposes Y_MAX constant to JSF pages for validation
     */
    public int getY_MAX() {
        return Orb.Y_MAX;
    }

    /**
     * Exposes SIZE_MAX constant to JSF pages for validation
     */
    public int getSIZE_MAX() {
        return Orb.SIZE_MAX;
    }

    /**
     * Exposes MAX_SPEED constant to JSF pages for validation
     */
    public int getMAX_SPEED() {
        return Orb.MAX_SPEED;
    }

    /**
     * Exposes INITIAL_SIZE constant to JSF pages
     */
    public int getINITIAL_SIZE() {
        return Orb.INITIAL_SIZE;
    }

    /**
     * Exposes DECAY_RATE constant to JSF pages
     */
    public int getDECAY_RATE() {
        return Orb.DECAY_RATE;
    }

    // -------------------- Existing Methods --------------------

    public Orb getSelected() {
        if (selected == null) {
            selected = new Orb();
            selectedItemIndex = -1;
        }
        return selected;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return orbFacade.count();
                }

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

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        selected = new Orb();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            orbFacade.create(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle").getString("OrbCreated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            orbFacade.edit(selected);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("OrbUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        selected = getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

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

    private void performDestroy() {
        try {
            Orb toDelete = orbFacade.find(selected.getId());
            if (toDelete != null) {
                orbFacade.remove(toDelete);
            }
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("OrbDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = orbFacade.count();
        if (selectedItemIndex >= count) {
            selectedItemIndex = count - 1;
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            List<Orb> list = orbFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1});
            selected = list.isEmpty() ? null : list.get(0);
        }
    }

    public DataModel<Orb> getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(orbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(orbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Orb.class)
    public static class OrbControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            OrbController controller = (OrbController) facesContext.getApplication().getELResolver()
                    .getValue(facesContext.getELContext(), null, "orbController");
            return controller.orbFacade.find(Long.valueOf(value));
        }

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
package cst8218.chab0109.orbgame.business;

import cst8218.chab0109.orbgame.entity.UserAccount;
import cst8218.chab0109.orbgame.presentation.util.JsfUtil;
import cst8218.chab0109.orbgame.presentation.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;

/**
 * JSF Controller that handles creating, reading, updating, and deleting UserAccount
 * entities through the UserAccountJpaController. It also manages pagination and
 * the currently selected UserAccount during the JSF session.
 */
@Named("userAccountController")
@SessionScoped
public class UserAccountController implements Serializable {

    /** Handles manual JTA transactions (since no EJB facade is used) */
    @Resource
    private UserTransaction utx = null;

    /** PersistenceUnit used to create EntityManagerFactory instances */
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    /** Holds the currently selected UserAccount entity */
    private UserAccount current;

    /** DataModel used for displaying paginated user lists in JSF pages */
    private DataModel items = null;

    /** JPA controller responsible for performing DB operations */
    private UserAccountJpaController jpaController = null;

    /** Helper class for pagination (page size, next/previous page, etc.) */
    private PaginationHelper pagination;

    /** Index of the selected item in the paginated list */
    private int selectedItemIndex;

    /** Default constructor */
    public UserAccountController() {
    }

    /**
     * Returns the currently selected UserAccount object.
     * Creates a new one if none exists yet.
     */
    public UserAccount getSelected() {
        if (current == null) {
            current = new UserAccount();
            selectedItemIndex = -1;
        }
        return current;
    }

    /**
     * Lazily initializes and returns the JPA controller used to interact with the DB.
     */
    private UserAccountJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new UserAccountJpaController(utx, emf);
        }
        return jpaController;
    }

    /**
     * Creates and returns a PaginationHelper instance (page size = 10).
     * This controls how many elements appear per page in JSF tables.
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                /** Returns total number of UserAccount rows in the DB */
                @Override
                public int getItemsCount() {
                    return getJpaController().getUserAccountCount();
                }

                /** Loads the rows for the current page into a DataModel */
                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(
                        getJpaController().findUserAccountEntities(
                            getPageSize(),
                            getPageFirstItem()
                        )
                    );
                }
            };
        }
        return pagination;
    }

    /**
     * Navigates to the "List" page and reloads model data.
     */
    public String prepareList() {
        recreateModel();
        return "List";
    }

    /**
     * Navigates to the "View" page for the selected UserAccount.
     */
    public String prepareView() {
        current = (UserAccount) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        return "View";
    }

    /**
     * Prepares an empty UserAccount for the Create form.
     */
    public String prepareCreate() {
        current = new UserAccount();
        selectedItemIndex = -1;
        return "Create";
    }

    /**
     * Attempts to create a new UserAccount in the DB.
     * Shows success or error messages depending on outcome.
     */
    public String create() {
        try {
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(
                ResourceBundle.getBundle("/Bundle").getString("UserAccountCreated")
            );
            return prepareCreate(); // Reset form
        } catch (Exception e) {
            JsfUtil.addErrorMessage(
                e,
                ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured")
            );
            return null;
        }
    }

    /**
     * Loads the Edit page for the selected user.
     */
    public String prepareEdit() {
        current = (UserAccount) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        return "Edit";
    }

    /**
     * Saves changes to an existing UserAccount.
     */
    public String update() {
        try {
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(
                ResourceBundle.getBundle("/Bundle").getString("UserAccountUpdated")
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
     * Deletes the selected UserAccount from the DB.
     */
    public String destroy() {
        current = (UserAccount) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + 
                            getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    /**
     * Deletes user and returns to correct page depending on what remains.
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
     * Performs the actual deletion and handles error messages.
     */
    private void performDestroy() {
        try {
            getJpaController().destroy(current.getId());
            JsfUtil.addSuccessMessage(
                ResourceBundle.getBundle("/Bundle").getString("UserAccountDeleted")
            );
        } catch (Exception e) {
            JsfUtil.addErrorMessage(
                e,
                ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured")
            );
        }
    }

    /**
     * Updates the selected item index after deletions or page changes.
     */
    private void updateCurrentItem() {
        int count = getJpaController().getUserAccountCount();
        if (selectedItemIndex >= count) {
            selectedItemIndex = count - 1;

            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController()
                    .findUserAccountEntities(1, selectedItemIndex)
                    .get(0);
        }
    }

    /**
     * Returns the current DataModel of users for the JSF table.
     */
    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    /** Forces the DataModel to reload its data */
    private void recreateModel() {
        items = null;
    }

    /** Resets pagination state */
    private void recreatePagination() {
        pagination = null;
    }

    /** Moves to next page of user list */
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    /** Moves to previous page of user list */
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    /** Used in JSF drop-down lists (multi-select) */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(
                getJpaController().findUserAccountEntities(), false);
    }

    /** Used in JSF drop-down lists (single-select) */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(
                getJpaController().findUserAccountEntities(), true);
    }

    /**
     * Converter so JSF can convert between UserAccount objects and their
     * primary key values when selecting from menus or forms.
     */
    @FacesConverter(forClass = UserAccount.class)
    public static class UserAccountControllerConverter implements Converter {

        /** Converts String ID → UserAccount object */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserAccountController controller = (UserAccountController) facesContext
                    .getApplication().getELResolver()
                    .getValue(facesContext.getELContext(), null, "userAccountController");
            return controller.getJpaController().findUserAccount(getKey(value));
        }

        /** Converts String to Long */
        java.lang.Long getKey(String value) {
            return Long.valueOf(value);
        }

        /** Converts UserAccount → String ID */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserAccount) {
                UserAccount o = (UserAccount) object;
                return o.getId().toString();
            } else {
                throw new IllegalArgumentException(
                        "Object " + object.getClass().getName() +
                        " is not a UserAccount");
            }
        }
    }
}

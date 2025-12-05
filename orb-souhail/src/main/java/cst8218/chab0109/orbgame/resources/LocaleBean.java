package cst8218.chab0109.orbgame.resources;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named("localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

    private String language = "en";  // default

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Called by AJAX to change the locale
    public void updateLocale() {
        FacesContext.getCurrentInstance()
                .getViewRoot()
                .setLocale(new Locale(language));
    }
}

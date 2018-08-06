package ru.it_mm.winpak.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.validate.ClientValidator;

@FacesValidator("utils.pathValidator")
public class PathValidator implements Validator, ClientValidator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) { return; }
        try {
            Paths.get(String.valueOf(value));
        } catch (InvalidPathException | NullPointerException ex) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Поле 'Каталог' заполнено не верно", ex.toString()));
        }
        
        if (String.valueOf(value).contains("/")){            
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Поле 'Каталог' не может содержать символ /",
                        "Поле 'Каталог' не может содержать символ /"));
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "utils.pathValidator";
    }
    
}

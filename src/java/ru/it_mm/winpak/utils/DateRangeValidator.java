package ru.it_mm.winpak.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.validate.ClientValidator;

@FacesValidator("utils.dateRangeValidator")
public class DateRangeValidator implements Validator, ClientValidator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {        
        if (value == null) { return; }
        
        Object startDateValue = component.getAttributes().get("date1");
        if (startDateValue==null) {
            return;
        }      
        
        Date date1 = (Date)startDateValue;
        Date date2 = (Date)value; 
        
        LocalDateTime minDate = LocalDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault());
        LocalDateTime maxDate = LocalDateTime.ofInstant(date2.toInstant(), ZoneId.systemDefault());       
        
        Duration diff = Duration.between(minDate, maxDate);
        if (diff.toDays() > 230){ //максимум столбцов в excel 256 =>
            if (((org.primefaces.component.calendar.Calendar) component).getClientId().contains("2")){
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Период охватывает " + String.valueOf(diff.toDays()) + " дней!",
                        "Отчет может содежать только 256 колонок"));
            }
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "utils.dateRangeValidator";
    }
    
}

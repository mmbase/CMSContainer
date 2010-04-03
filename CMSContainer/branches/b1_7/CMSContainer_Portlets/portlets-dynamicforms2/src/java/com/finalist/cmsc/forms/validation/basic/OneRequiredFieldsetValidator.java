package com.finalist.cmsc.forms.validation.basic;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.definition.GuiField;
import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.FieldsetValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;
import com.finalist.cmsc.forms.value.ValueObject;


public class OneRequiredFieldsetValidator extends FieldsetValidator {

   private final static Log Log = LogFactory.getLog(OneRequiredFieldsetValidator.class);

   public OneRequiredFieldsetValidator() {
      super("validator.fieldset.onerequired.message");
   }

   @Override
   public boolean validate(List<GuiField> fields, ValueObject object) {
      boolean valid = false;
      FieldValidator validator = new RequiredValidator();
      for (GuiField guiField : fields) {
         ValueField valueField = object.getField(guiField.getName());
         if (valueField == null) {
            Log.error("Object (" + object.getName() + ") does not contain field :" + guiField.getName()
                  + " - unable to validate object.");
            return false;
         }
         if (validator.validate(valueField)) {
            valid = true;
         }
      }
      if (!valid) {
         for (GuiField guiField : fields) {
            ValueField valueField = object.getField(guiField.getName());
            ValidationError errorMsg = getErrorMessage(valueField);
            valueField.setValidationError(errorMsg);
         }
      }
      return valid;
   }

}

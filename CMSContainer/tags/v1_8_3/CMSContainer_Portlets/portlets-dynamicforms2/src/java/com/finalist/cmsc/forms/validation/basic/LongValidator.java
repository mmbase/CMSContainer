package com.finalist.cmsc.forms.validation.basic;

import java.math.BigDecimal;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;


public class LongValidator extends FieldValidator {

   public LongValidator() {
      super("validator.long.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      long value = field.getLongValue();
      
      double minimumValue = field.getMin();
      double maximumValue = field.getMax();
      if (!Double.isNaN(maximumValue) && !Double.isNaN(minimumValue)) {
         return minimumValue <= value && value <= maximumValue;
      }
      if (!Double.isNaN(minimumValue)) {
         return minimumValue <= value;
      }
      if (!Double.isNaN(maximumValue)) {
         return value <= maximumValue;
      }
      return true;
   }

   @Override
   public ValidationError getErrorMessage(ValueField valueField) {
      double minimumValue = valueField.getMin();
      double maximumValue = valueField.getMax();
      if (!Double.isNaN(maximumValue) && !Double.isNaN(minimumValue)) {
         ValidationError error = new ValidationError("validator.long.message");
         BigDecimal min = new BigDecimal(minimumValue);
         BigDecimal max = new BigDecimal(maximumValue);
         error.addParam(min);
         error.addParam(max);
         return error;
      }
      if (!Double.isNaN(maximumValue)) {
         ValidationError error = new ValidationError("validator.long.max.message");
         BigDecimal max = new BigDecimal(maximumValue);
         error.addParam(max);
         return error;
      }
      if (!Double.isNaN(minimumValue)) {
         ValidationError error = new ValidationError("validator.long.min.message");
         BigDecimal min = new BigDecimal(minimumValue);
         error.addParam(min);
         return error;
      }
      return super.getErrorMessage();
   }
}

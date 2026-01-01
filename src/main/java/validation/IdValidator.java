package validation;

import exceptions.ValidationException;

public class IdValidator implements Validator<Long>{
    @Override
    public void validate(Long entity) throws ValidationException {
        if(entity == null || entity<0){
            throw new ValidationException("Invalid id!");
        }
    }
}

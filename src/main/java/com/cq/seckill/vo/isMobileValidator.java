package com.cq.seckill.vo;

import com.cq.seckill.utils.ValidatorUtil;
import com.cq.seckill.validator.isMobile;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class isMobileValidator implements ConstraintValidator<isMobile,String> {
    private boolean required = false;

    @Override
    public void initialize(isMobile constraintAnnotation) {
        required = constraintAnnotation.required();//校验的数据是否为必填项
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s)){
                return true;//如果不是必填的数据，我空着也要能过,因此返回true
            }else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}

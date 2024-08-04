package satisfied.be.dont.pow.auth.annotation.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.passay.*
import satisfied.be.dont.pow.auth.annotation.Password

/**
 * Bean Validation이란.
 *
 * 특정 구현체가 아닌 Bean Validation 2.0(JSR-380)이라는 기술 표준으로 여러 검증 애노테이션과 여러 인터페이스의 모음이다. (이러한 Bean Validation을 구현한 기술중 일반적으로 사용하는 구현체는 하이버네이트 Validator이다.)
 *
 * 기본적으로 LocalValidatorFactoryBean은 스프링을 사용하여 ConstraintValidator인스턴스를 생성하는 SpringConstraintValidatorFactory를 구성합니다.
 * 이를 통해 사용자 정의 ConstraintValidators는 다른 스프링 빈처럼 의존성 주입의 이점을 얻을 수 있습니다.
 *
 * LocalValidatorFactoryBean은 targetConstraintValidatorFactory 변수를 통해 ConstraintValidatorFactory를 저장하고, 이 변수는 SpringConstraintValidatorFactory를 통해 설정됩니다.
 * SpringConstraintValidatorFactory는 Spring의 AutowireCapableBeanFactory를 사용하여 ConstraintValidator 인스턴스를 생성하고 관리합니다.
 *
 * 이 과정을 통해 Spring Boot는 유효성 검증 애노테이션이 붙은 필드에 대해 자동으로 유효성 검증을 수행할 수 있으며, Spring 컨텍스트에서 관리되는 빈들이 ConstraintValidator에 주입될 수 있습니다.
 *
 */
class PasswordConstraintValidator: ConstraintValidator<Password, String> {

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        val validator = PasswordValidator(
            listOf(
                LengthRule(),
                CharacterRule(EnglishCharacterData.UpperCase, 1),
                CharacterRule(EnglishCharacterData.LowerCase, 1),
                CharacterRule(EnglishCharacterData.Digit, 1),
                CharacterRule(EnglishCharacterData.Special, 1),
                WhitespaceRule()
            )
        )
        val result = validator.validate(PasswordData(value))
        if (result.isValid) {
            return true
        }

        context.buildConstraintViolationWithTemplate(validator.getMessages(result).stream().findFirst().get())
            .addConstraintViolation()
            .disableDefaultConstraintViolation()

        return false
    }

}
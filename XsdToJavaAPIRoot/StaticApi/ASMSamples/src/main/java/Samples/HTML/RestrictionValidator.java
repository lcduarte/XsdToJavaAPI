package Samples.HTML;

import java.util.List;
import java.util.Map;

public final class RestrictionValidator {

    private RestrictionValidator() {}

    static void validateList(Map<String, Object> restriction, Object list){
        List listObj = (List) list;

        validateLength((int) restriction.getOrDefault("Length", -1), listObj);
        validateMinLength((int) restriction.getOrDefault("MinLength", -1), listObj);
        validateMaxLength((int) restriction.getOrDefault("MaxLength", -1), listObj);
    }

    static void validateNumeric(Map<String, Object> restriction, Object value){
        Double doubleObj = (Double) value;

        validateMaxExclusive((int)restriction.getOrDefault("MaxExclusive", -1), doubleObj);
        validateMaxInclusive((int)restriction.getOrDefault("MaxInclusive", -1), doubleObj);
        validateMinExclusive((int)restriction.getOrDefault("MinExclusive", -1), doubleObj);
        validateMinInclusive((int)restriction.getOrDefault("MinInclusive", -1), doubleObj);
        validateFractionDigits((int)restriction.getOrDefault("FractionDigits", -1), doubleObj);
        validateTotalDigits((int)restriction.getOrDefault("TotalDigits", -1), doubleObj);
    }

    static void validateString(Map<String, Object> restriction, Object string){
        String stringObj = (String) string;

        validateLength((int) restriction.getOrDefault("Length", -1), stringObj);
        validateMinLength((int) restriction.getOrDefault("MinLength", -1), stringObj);
        validateMaxLength((int) restriction.getOrDefault("MaxLength", -1), stringObj);
        validatePattern((String) restriction.getOrDefault("Pattern", null), stringObj);
    }

    private static void validateFractionDigits(int fractionDigits, double value){
        if (fractionDigits == -1){
            return;
        }

        if (value != ((int) value)){
            String doubleValue = String.valueOf(value);

            int numberOfFractionDigits = doubleValue.substring(doubleValue.indexOf(",")).length();

            if (numberOfFractionDigits > fractionDigits){
                throw new RestrictionViolationException("Violation of fractionDigits restriction, value should have a maximum of " + fractionDigits + " decimal places.");
            }
        }
    }

    private static void validateLength(int length, String string){
        if (length == -1){
            return;
        }

        if (string.length() != length){
            throw new RestrictionViolationException("Violation of length restriction, string should have exactly " + length + " characters.");
        }
    }

    private static void validateLength(int length, List list){
        if (length == -1){
            return;
        }

        if (list.size() != length){
            throw new RestrictionViolationException("Violation of length restriction, list should have exactly " + length + " elements.");
        }
    }

    private static void validateMaxExclusive(int maxExclusive, double value){
        if (maxExclusive == -1){
            return;
        }

        if (value >= maxExclusive){
            throw new RestrictionViolationException("Violation of maxExclusive restriction, value should be lesser than " + maxExclusive);
        }
    }

    private static void validateMaxInclusive(int maxInclusive, double value){
        if (maxInclusive == -1){
            return;
        }

        if (value > maxInclusive){
            throw new RestrictionViolationException("Violation of maxInclusive restriction, value should be lesser or equal to " + maxInclusive);
        }
    }

    private static void validateMaxLength(int maxLength, String string){
        if (maxLength == -1){
            return;
        }

        if (string.length() > maxLength){
            throw new RestrictionViolationException("Violation of maxLength restriction, string should have a max number of characters of " + maxLength);
        }
    }

    private static void validateMaxLength(int maxLength, List list){
        if (maxLength == -1){
            return;
        }

        if (list.size() > maxLength){
            throw new RestrictionViolationException("Violation of maxLength restriction, list should have a max number of items of " + maxLength);
        }
    }

    private static void validateMinExclusive(int minExclusive, double value){
        if (minExclusive == -1){
            return;
        }

        if (value <= minExclusive){
            throw new RestrictionViolationException("Violation of minExclusive restriction, value should be greater than " + minExclusive);
        }
    }

    private static void validateMinInclusive(int minInclusive, double value){
        if (minInclusive == -1){
            return;
        }

        if (value < minInclusive){
            throw new RestrictionViolationException("Violation of minInclusive restriction, value should be greater or equal to " + minInclusive);
        }
    }

    private static void validateMinLength(int minLength, String string){
        if (minLength == -1){
            return;
        }

        if (string.length() < minLength){
            throw new RestrictionViolationException("Violation of minLength restriction, string should have a minimum number of characters of " + minLength);
        }
    }

    private static void validateMinLength(int minLength, List list){
        if (minLength == -1){
            return;
        }

        if (list.size() < minLength){
            throw new RestrictionViolationException("Violation of minLength restriction, list should have a minimum number of items of " + minLength);
        }
    }

    private static void validatePattern(String pattern, String string){
        if (pattern == null){
            return;
        }

        if (!string.replaceAll(pattern, "").equals(string)){
            throw new RestrictionViolationException("Violation of pattern restriction, the string doesn't math the acceptable pattern, which is " + pattern);
        }
    }

    private static void validateTotalDigits(int totalDigits, double value){
        if (totalDigits == -1){
            return;
        }

        String doubleValue = String.valueOf(value);

        int numberOfDigits;

        if (value != ((int) value)){
            numberOfDigits = doubleValue.length() - 1;


        } else {
            numberOfDigits = doubleValue.length();
        }

        if (numberOfDigits != totalDigits){
            throw new RestrictionViolationException("Violation of fractionDigits restriction, value should have a exactly " + totalDigits + " decimal places.");
        }
    }
}

package dev.dokan.dokan_java.conv;


public interface MaskValueEnum extends EnumInteger {

    static <T extends Enum<T> & MaskValueEnum> T maskEnumFromInt(final int maskingValue, final T[] enumValues) {
        return EnumInteger.enumFromInt(maskingValue, enumValues);
    }

    static <T extends Enum<T> & MaskValueEnum> MaskValueSet<T> maskValueSet(final int mask, final T[] allEnumValues) {
        return MaskValueSet.maskValueSet(mask, allEnumValues);
    }

    static <T extends Enum<T> & MaskValueEnum> MaskValueSet<T> maskValueSet(final int mask, final Class<T> type) {
        return MaskValueSet.maskValueSet(mask, type);
    }

    default int getMaskingValue() {
        return intValue();
    }
}
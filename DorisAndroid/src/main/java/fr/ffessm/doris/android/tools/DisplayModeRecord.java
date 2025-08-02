package fr.ffessm.doris.android.tools;

public record DisplayModeRecord(
        String value,
        String label,
        String details
) {
    @Override
    public String value() {
        return value;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public String details() {
        return details;
    }
}

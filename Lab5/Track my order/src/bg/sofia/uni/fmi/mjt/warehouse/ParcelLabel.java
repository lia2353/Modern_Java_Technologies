package bg.sofia.uni.fmi.mjt.warehouse;

import java.time.LocalDateTime;
import java.util.Objects;

public class ParcelLabel<L> {
    private L label;
    private LocalDateTime submissionDate;
    private boolean expired;

    public ParcelLabel(L label, LocalDateTime submissionDate) {
        this.label = label;
        this.submissionDate = submissionDate;
    }

    public L getLabel() {
        return label;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public boolean isExpired(int retentionPeriod) {
        if (expired) {
            return true;
        }

        LocalDateTime expirationDate = submissionDate.plusDays(retentionPeriod);
        expired = expirationDate.isBefore(LocalDateTime.now());
        return expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParcelLabel)) return false;
        ParcelLabel<?> that = (ParcelLabel<?>) o;
        return label.equals(that.label) &&
                submissionDate.equals(that.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, submissionDate);
    }
}

package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {
    private int capacity;
    private int retentionPeriod;

    private SortedMap<ParcelLabel<L>, P> parcels;

    /**
     * Creates a new instance of MJTExpressWarehouse with the given characteristics
     *
     * @param capacity        the total number of parcels that the warehouse can store
     * @param retentionPeriod the maximum number of days for which a parcel can stay in the warehouse, counted from
     *                        the day the parcel was submitted. After that time passes, the parcel can be removed from
     *                        the warehouse
     */
    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        this.parcels = new TreeMap<>(new Comparator<ParcelLabel<L>>() {
            @Override
            public int compare(ParcelLabel<L> p1, ParcelLabel<L> p2) {
                if (p1.getSubmissionDate() != null && p2.getSubmissionDate() != null) {
                    int datesCompare = p1.getSubmissionDate().compareTo(p2.getSubmissionDate());
                    if (datesCompare != 0) {
                        return datesCompare;
                    }
                }

                if (p1.getLabel() != null && p2.getLabel() != null && p1.getLabel().equals(p2.getLabel())) {
                    // the labels are identical
                    return 0;
                }

                // the labels are not the same, but the dates are, hence the ordering does not matter
                return 1;
            }
        });
    }


    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {
        if (label == null || parcel == null || submissionDate == null) {
            throw new IllegalArgumentException("Provided arguments cannot be null.");
        }
        if (submissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Provided submission date cannot be in the future.");
        }
        if (capacity == parcels.size()) {
            if (parcels.firstKey().getSubmissionDate().plusDays(retentionPeriod).isAfter(LocalDateTime.now())) {
                throw new CapacityExceededException("There is no more free space. The warehouse is with" + capacity + " capacity.");
            }
            parcels.remove(parcels.firstKey());
        }

        parcels.put(new ParcelLabel<>(label, submissionDate), parcel);
    }

    @Override
    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException("Provided label cannot be null.");
        }

        return parcels.get(wrapLabel(label));
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException("Provided label cannot be null.");
        }

        ParcelLabel<L> parcelKey = wrapLabel(label);
        P parcel = parcels.get(parcelKey);
        if (parcel == null) {
            throw new ParcelNotFoundException("Parcel with label " + label + " does not exist in the warehouse.");
        }

        parcels.remove(parcelKey);
        return parcel;
    }

    @Override
    public double getWarehouseSpaceLeft() {

        double freeSpace = (double) 1 - parcels.size() / (double) capacity;
        return Math.round(freeSpace * 100.0) / 100.0; //rounds to two decimal places
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        Map<L, P> allParcels = new HashMap<>();
        for (Map.Entry<ParcelLabel<L>, P> p : parcels.entrySet()) {
            allParcels.put(p.getKey().getLabel(), p.getValue());
        }
        return allParcels;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException("Provided before date cannot be null.");
        }

        if (before.isAfter(LocalDateTime.now())) {
            return getWarehouseItems();
        }

        SortedMap<ParcelLabel<L>, P> parcelsSubmittedBefore = parcels.headMap(new ParcelLabel<>(null, before));
        return deliverParcels(parcelsSubmittedBefore);
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException("The given date is null.");
        }

        if (after.isAfter(LocalDateTime.now())) {
            return Collections.emptyMap();
        }

        SortedMap<ParcelLabel<L>, P> parcelsSubmittedAfter = parcels.tailMap(new ParcelLabel<>(null, after));
        return deliverParcels(parcelsSubmittedAfter);
    }

    private ParcelLabel<L> wrapLabel(L label) {
        return new ParcelLabel<>(label, null);
    }

    private Map<L, P> deliverParcels(SortedMap<ParcelLabel<L>, P> map) {
        Map<L, P> parcelsToBeDelivered = new HashMap<>();

        for (Map.Entry<ParcelLabel<L>, P> entry : map.entrySet()) {
            parcelsToBeDelivered.put(entry.getKey().getLabel(), entry.getValue());
            parcels.remove(entry.getKey());
        }

        return parcelsToBeDelivered;
    }
}

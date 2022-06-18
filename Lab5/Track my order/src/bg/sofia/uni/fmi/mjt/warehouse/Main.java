package bg.sofia.uni.fmi.mjt.warehouse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ParcelLabel<String> p1 = new ParcelLabel<>("p1", LocalDateTime.of(2000, 4, 10, 12, 30));
        ParcelLabel<String> p2 = new ParcelLabel<>("p2", LocalDateTime.of(2010, 4, 10, 12, 30));
        ParcelLabel<String> p3 = new ParcelLabel<>("p3", LocalDateTime.of(2020, 11, 10, 12, 30));
        ParcelLabel<String> p4 = new ParcelLabel<>("p4", LocalDateTime.of(2030, 4, 10, 12, 30));

        DeliveryServiceWarehouse<String, Integer> wh = new MJTExpressWarehouse(6, 300);
        System.out.println(wh.getParcel("p1"));

        try {
            //wh.deliverParcel(null);
            wh.deliverParcel("p1");
        }catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(wh.getWarehouseSpaceLeft());
        System.out.println(wh.getWarehouseItems());
        System.out.println(wh.deliverParcelsSubmittedBefore(LocalDateTime.of(2010, 11, 10, 12, 30)));
        System.out.println(wh.deliverParcelsSubmittedAfter(LocalDateTime.of(2010, 11, 10, 12, 30)));

        try {
            wh.submitParcel("p1", 10, LocalDateTime.of(2000, 4, 10, 12, 30));
            wh.submitParcel("p2", 20, LocalDateTime.of(2010, 4, 10, 12, 30));
            wh.submitParcel("p3", 100, LocalDateTime.of(2020, 11, 10, 12, 30));
            //wh.submitParcel("p4", 10, LocalDateTime.of(2030, 4, 10, 12, 30));
            wh.submitParcel("p4", 10, LocalDateTime.of(2020, 10, 10, 12, 30));
        } catch (Exception e) {
            System.out.println(e);
        }

        Set<Map.Entry<String, Integer>> hhh = wh.getWarehouseItems().entrySet();
        for (Map.Entry<String, Integer> p : hhh) {
            System.out.println(p.getKey() + " " + p.getValue());
        }

        System.out.println(wh.getParcel("p4"));
//        hhh = wh.getWarehouseItems().entrySet();
//        for (Map.Entry<String, Integer> p : hhh) {
//            System.out.println(p.getKey() + " " + p.getValue());
//        }
        System.out.println(wh.getParcel("p2"));
//        System.out.println(wh.getWarehouseSpaceLeft());
//        try {
//            wh.deliverParcel("p1");
//        }catch (Exception e) {
//            System.out.println(e);
//        }
//        System.out.println(wh.getWarehouseSpaceLeft());

/*
        System.out.println(wh.getWarehouseItems());
       System.out.println(wh.deliverParcelsSubmittedBefore(LocalDateTime.of(2010, 11, 10, 12, 30)));
//        System.out.println(wh.deliverParcelsSubmittedAfter(LocalDateTime.of(2010, 11, 10, 12, 30)));
*/

    }

}

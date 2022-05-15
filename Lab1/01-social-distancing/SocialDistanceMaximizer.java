package social.distancing;

public class SocialDistanceMaximizer_mySolution {
    public static int maxDistance(int[] seats) {
        int maxDistance = 0;
        int currentMaxDistance = 0;

        int firstSeat = 0;
        int lastSeat = seats.length  - 1;

        // Free seats at the beginning of the row
        while (firstSeat < lastSeat && seats[firstSeat] == 0) {
            ++maxDistance;
            ++firstSeat;
        }

        // Free seats at the end of the row
        while (firstSeat < lastSeat && seats[lastSeat] == 0) {
            ++currentMaxDistance;
            --lastSeat;
        }
        maxDistance = Math.max(currentMaxDistance, maxDistance);
        currentMaxDistance = 0;

        // Free seats in the middle of the row
        for (int i = firstSeat; i <= lastSeat; ++i) {
            if (seats[i] == 0) {
                ++currentMaxDistance;
            } else {
                // Half and ceil currentDistance
                currentMaxDistance = (currentMaxDistance + 1) / 2;
                maxDistance = Math.max(currentMaxDistance, maxDistance);
                currentMaxDistance = 0;
            }
        }

        return maxDistance;
    }
}

public class WeatherForecaster {
    // O(N^2)
    public static int[] getsWarmerIn(int[] temperatures) {
        if (temperatures == null) {
            return new int[]{};
        }

        int daysCount = temperatures.length;
        int[] nextWarmerDayAfter = new int[daysCount];

        for (int day = 0; day < daysCount - 1; ++day) {
            for (int nextDay = day + 1; nextDay < daysCount; ++nextDay) {
                if (temperatures[day] < temperatures[nextDay]) {
                    nextWarmerDayAfter[day] = nextDay - day;
                    break;
                }
            }
        }
        return nextWarmerDayAfter;
    }

    // O(N)
    public static int[] getsWarmerIn2(int[] temperatures) {
        if (temperatures == null) {
            return new int[]{};
        }

        int daysCount = temperatures.length;
        int[] nextWarmerDayAfter = new int[daysCount];

        // nextWarmerDayAfter[daysCount - 1] is always 0
        // We know next warmer day for days i, (i + 1), ... (daysCount -1) and will find for day (i - 1)
        for (int i = daysCount - 1; i > 0; --i) {
            if (temperatures[i - 1] < temperatures[i]) {
                nextWarmerDayAfter[i - 1] = 1;
            } else {
                int warmerDay = i + nextWarmerDayAfter[i];

                while (warmerDay < daysCount) {
                    if (temperatures[i - 1] < temperatures[warmerDay]) {
                        nextWarmerDayAfter[i - 1] = warmerDay - i + 1;
                        break;
                    }

                    if (nextWarmerDayAfter[warmerDay] == 0) {
                        nextWarmerDayAfter[i - 1] = 0;
                        break;
                    }

                    warmerDay += nextWarmerDayAfter[warmerDay];
                }
            }
        }
        return nextWarmerDayAfter;
    }
    
}

package vehiclessharing.vehiclessharing.model;

/**
 * Created by Tuan on 26/04/2017.
 */

/**
 * birhtday's user
 */
public class BirthDay {
    int day;
    int month;
    int year;

    public BirthDay() {
    }

    public BirthDay(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Compare between two object birthday
     * @param birthDay
     * @return true - both birthday is match
     */
    public boolean compareBirhtday(BirthDay birthDay){
        if(this.day != birthDay.getDay() ||
                this.month != birthDay.getMonth() ||
                this.year != birthDay.getYear()) return false;
        return true;
    }
}

package vehiclessharing.vehiclessharing.model;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vehiclessharing.vehiclessharing.constant.Utils;


/**
 * Created by Tuan on 02/04/2017.
 */

public class Validation {
    private Boolean isValid;
    private String messageValid;

    public Validation() {
    }

    public Validation(Boolean isValid, String messageValid) {
        this.isValid = isValid;
        this.messageValid = messageValid;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getMessageValid() {
        return messageValid;
    }

    public void setMessageValid(String messageValid) {
        this.messageValid = messageValid;
    }

    /**
     * Check field is empty or not
     * @param value input to check
     * @return true - valid
     */
    public static boolean isEmpty(String value){
        if(value.equals("") || value.length() == 0)
            return true;
        return false;
    }

    /**
     * Check if email is valid or not
     * @param email user's email
     * @return object Validation
     */
    public static Validation checkValidEmail(String email){
        Pattern p = Pattern.compile(Utils.regEx);// Check patter for email
        Matcher m = p.matcher(email);// Check patter for email
        if (!m.find())
            return new Validation(false,"Email is invalid!");
        return new Validation(true,"");
    }

    /**
     * Check if phone number is valid or not
     * @param number phonenumber's user
     * @return true - valid
     */
    public static Validation checkValidPhone(String number){
        Pattern pattern = Pattern.compile(Utils.regPx);
        Matcher matcher = pattern.matcher(number);
        if (!matcher.matches()) {
            return new Validation(false,"PhoneNumber is invalid!");
        } else
        if (number.length() == 10 || number.length() == 11) {
            if (number.length() == 10) {
                if (number.substring(0, 2).equals("09")) {
                    return new Validation(true,"");
                } else {
                    return new Validation(false,"PhoneNumber is invalid!");
                }
            } else
            if (number.substring(0, 2).equals("01")) {
                return new Validation(true,"");
            } else {
                return new Validation(false,"PhoneNumber is invalid!");
            }
        } else {
            return new Validation(false,"PhoneNumber is invalid!");
        }
    }


    /**
     * Check if password is valid or not
     * @param password password's user
     * @return true - valid
     */
    public static Validation checkValidPassword(String password){
        if(password.length() < 6){
            return new Validation(false,"Your Password must be at least 6 characters!");
        }
        return new Validation(true, "");
    }


    /**
     * Check if password and confirmpassword is match.
     * @param password password
     * @param confirmPassword confirmpassowrd
     * @return true - match
     */
    public static Validation checkValidConfirmPassword(String password, String confirmPassword){
        if(!confirmPassword.equals(password)){
            return new Validation(false,"Both password doesn't match!");
        }
        return new Validation(true, "");
    }

    /**
     *
     * @param birthDay
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Validation checkValidBirthDay(BirthDay birthDay){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if(birthDay.getYear() > year) return new Validation(false,"Birthday is invalid!");
        else {
            if(birthDay.getMonth() > month) return new Validation(false,"Birthday is invalid!");
            else if(birthDay.getDay() > day) return new Validation(false,"Birthday is invalid!");
        }
        return new Validation(true, "");
    }
}

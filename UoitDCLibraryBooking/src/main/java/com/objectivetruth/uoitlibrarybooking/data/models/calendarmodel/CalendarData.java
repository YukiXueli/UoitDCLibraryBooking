package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

import java.util.List;

public class CalendarData {
    public List<CalendarDay> days;
    public String viewstatemain;
    public String eventvalidation;
    public String viewstategenerator;

    @Override
    public String toString() {
        String returnString = "";
        returnString += "viewstatemain: ";
        if(notNull(viewstatemain)) {returnString += viewstatemain + ", ";} else {returnString += "NULL, ";}

        returnString += "eventvalidation: ";
        if(notNull(eventvalidation)) {returnString += eventvalidation + ", ";} else {returnString += "NULL, ";}

        returnString += "viewstategenerator: ";
        if(notNull(viewstategenerator)) {returnString += viewstategenerator + ", ";} else {returnString += "NULL, ";}

        returnString += "days:";
        if(days == null) {
            returnString += " NULL";
            return returnString;
        }

        if(_notEmpty(days)) {
            int i = 0;
            for(CalendarDay day: days) {
                if(notNull(day)){
                    returnString += " (" + i + ")" + day.toString();
                }else{
                    returnString += " (" + i + ")NULL";
                }
                i++;
            }
        }else{
            returnString += " EMPTY";
        }
        return returnString;
    }

    private boolean notNull(Object object) {
        return (object != null);
    }

    private boolean _notEmpty(List<CalendarDay> calendarDays) {
        return !calendarDays.isEmpty();
    }
}

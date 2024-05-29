package org.hse.mylaundryapplication;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ScheduleDiffUtilCallback extends DiffUtil.Callback {
    private final List<ScheduleItem> oldlist;
    private final List<ScheduleItem> newlist;

    public ScheduleDiffUtilCallback(List<ScheduleItem> oldlist, List<ScheduleItem> newlist) {
        this.oldlist=oldlist;
        this.newlist=newlist;

    }
    @Override
    public int getOldListSize() {
        return oldlist.size();
    }
    @Override
    public int getNewListSize() {
        return newlist.size();
    }
     @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ScheduleItem oldScheduleItem = oldlist.get(oldItemPosition);
        ScheduleItem newScheduleItem = newlist.get(newItemPosition);
        return oldScheduleItem.getFloor() == newScheduleItem.getFloor();
     }
     @Override
    public boolean areContentsTheSame(int  oldItemPosition, int newItemPosition ){

         ScheduleItem oldScheduleItem = oldlist.get(oldItemPosition);
         ScheduleItem newScheduleItem = newlist.get(newItemPosition);

         return
                 (oldScheduleItem.getFloor() == newScheduleItem.getFloor())&&
                         oldScheduleItem.getTime1() == newScheduleItem.getTime1() &&
                         oldScheduleItem.getTime2() == newScheduleItem.getTime2() &&
                         oldScheduleItem.getTime3() == newScheduleItem.getTime3() &&
                         oldScheduleItem.getTime4() == newScheduleItem.getTime4() &&
                         oldScheduleItem.getTime5() == newScheduleItem.getTime5() &&
                         oldScheduleItem.getTime6() == newScheduleItem.getTime6() ;

     }


}

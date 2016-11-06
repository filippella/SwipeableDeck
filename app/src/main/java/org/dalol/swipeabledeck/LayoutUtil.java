package org.dalol.swipeabledeck;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Filippo on 8/20/2016.
 */
public class LayoutUtil {

    public static ArrayList<View> flattenLayout(View view, boolean addViewGroups)
    {
        ArrayList<View> viewList = new ArrayList<View>();
        if(view instanceof ViewGroup)
        {
            if(((ViewGroup)view).getChildCount()==0)
                viewList.add(view);
            else
            {
                if(addViewGroups)
                {
                    viewList.add(view);
                }
                ViewGroup viewgroup = (ViewGroup) view;
                for(int i = 0; i < viewgroup.getChildCount();i++)
                {
                    viewList.addAll(flattenLayout(viewgroup.getChildAt(i),false));
                }
            }
        }
        else if(view instanceof View)
        {
            viewList.add(view);
        }
        return viewList;
    }
}

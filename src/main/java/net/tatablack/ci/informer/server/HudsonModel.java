/**
 *
 */
package net.tatablack.ci.informer.server;

import java.util.Collection;

import hudson.model.Hudson;
import hudson.model.View;
import net.tatablack.ci.informer.serialization.CiServerObject;

/**
 * @author Angelo Tata
 */
public class HudsonModel {
    public static String getViews(String[] viewNames) {
        StringBuilder jobsJSONArray = new StringBuilder("[");

        for (String viewName : viewNames) {
            jobsJSONArray.append(CiServerObject.toJSON(Hudson.getInstance().getView(viewName))).append(",");
        }

        jobsJSONArray.deleteCharAt(jobsJSONArray.length() - 1).append("]");

        return jobsJSONArray.toString();
    }

    public static String getViews(Collection<View> views) {
        StringBuilder jobArray = new StringBuilder("[");

        for (View view : views) {
            jobArray.append(CiServerObject.toJSON(view)).append(",");
        }

        jobArray.deleteCharAt(jobArray.length() - 1).append("]");

        return jobArray.toString();
    }
}

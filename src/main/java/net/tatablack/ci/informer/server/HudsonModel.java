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
        StringBuilder jobArray = new StringBuilder("[");

        for (String viewName : viewNames) {
            jobArray.append(CiServerObject.toJSON(Hudson.getInstance().getView(viewName))).append(",");
        }

        jobArray.deleteCharAt(jobArray.length() - 1).append("]");

        return jobArray.toString();
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

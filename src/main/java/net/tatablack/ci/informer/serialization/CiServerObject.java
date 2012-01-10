package net.tatablack.ci.informer.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;

import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Model;
import org.kohsuke.stapler.export.ModelBuilder;
import org.kohsuke.stapler.export.NamedPathPruner;
import org.kohsuke.stapler.export.TreePruner;

/**
 * @author Angelo Tata
 */
public class CiServerObject {
    private static final ModelBuilder MODEL_BUILDER = new ModelBuilder();
    private static final Logger logger = Logger.getLogger(CiServerObject.class.getName());

    private static final String JOB_MEMBERS = "displayName,name,buildable,color,inQueue,lastStableBuild[number],lastCompletedBuild[number]";
    private static final String VIEW_MEMBERS = "name,property,description,jobs[" + CiServerObject.JOB_MEMBERS + "]";

    public static final String EMPTY_RESULT = "[[]]";

    private static enum Path {
        Hudson( hudson.model.Hudson.class,
                "nodeDescription,views[name]"),

        View(    hudson.model.View.class,
                CiServerObject.VIEW_MEMBERS + ",views[" + CiServerObject.VIEW_MEMBERS + "]"),

        Job(    hudson.model.Job.class,
                CiServerObject.JOB_MEMBERS),

        Build(    hudson.model.AbstractBuild.class,
                "description,fullDisplayName,result,culprits");

        private Class<?> clazz;
        private String structure;

        private Path(Class<?> clazz, String structure) {
            this.clazz = clazz;
            this.structure = structure;
        }
    }

    public static String toJSON(Object item) {
        String result = EMPTY_RESULT;

        if(CiServerObject.isExportable(item)) {
            StringWriter writer = new StringWriter();

            Model model = CiServerObject.MODEL_BUILDER.get(item.getClass());

            try {
                model.writeTo(    item,
                                CiServerObject.getPruner(item),
                                new JSONDataWriter(writer));
                result = writer.toString();

                logger.finest(result);
            } catch (IOException ioEx) {
                logger.severe("Error while serializing an object of class " +
                        item.getClass() + ": " + ioEx.getMessage());
            }
        }

        return result;
    }

    private static TreePruner getPruner(Object item) {
        for (CiServerObject.Path path : CiServerObject.Path.values()) {
            logger.finer("Looking at path " + path.name() + " (" + path.clazz.getName() + ")");

            if(path.clazz.isInstance(item)) {
                return new NamedPathPruner(path.structure);
            }
        }

        return new TreePruner.ByDepth(1);
    }

    private static boolean isExportable(Object item) {
        boolean isExportable = false;

        if(item!=null) {
            Annotation annotation = item.getClass().getAnnotation(ExportedBean.class);
            isExportable = annotation instanceof ExportedBean;
            logger.finer("Checking whether an item of class " + item.getClass().getName() + " is exportable (" + isExportable + ")");
        }

        return isExportable;
    }
}

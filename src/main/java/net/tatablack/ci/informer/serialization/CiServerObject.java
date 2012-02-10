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
        		new NamedPathPruner("nodeDescription,views[" + CiServerObject.VIEW_MEMBERS + "]")),

        View(   hudson.model.View.class,
        		new NamedPathPruner(CiServerObject.VIEW_MEMBERS + ",views[" + CiServerObject.VIEW_MEMBERS + "]")),

        Job(    hudson.model.Job.class,
        		new NamedPathPruner(CiServerObject.JOB_MEMBERS)),

        Build(  hudson.model.AbstractBuild.class,
        		new NamedPathPruner("id,building,fullDisplayName,result,number,culprits[fullName]")),

        Unknown(null,
        		new TreePruner.ByDepth(1));

        private Class<?> clazz;
        private TreePruner pruner;

        private Path(Class<?> clazz, TreePruner pruner) {
            this.clazz = clazz;
            this.pruner = pruner;
        }
    }

    public static String toJSON(Object item) {
        String result = EMPTY_RESULT;

        if(CiServerObject.isExportable(item)) {
            StringWriter writer = new StringWriter();

            Model model = CiServerObject.MODEL_BUILDER.get(item.getClass());
            Path path = CiServerObject.getPath(item);

            try {
                model.writeTo(    item,
                                  path.pruner,
                                  new JSONDataWriter(writer, path.name()));
                result = writer.toString();

                logger.finest(result);
            } catch (IOException ioEx) {
                logger.severe("Error while serializing an object of class " +
                        item.getClass() + ": " + ioEx.getMessage());
            }
        }

        return result;
    }

    private static Path getPath(Object item) {
    	Path result = Path.Unknown;

        for (CiServerObject.Path path : CiServerObject.Path.values()) {
            logger.finer("Looking at path " + path.name() + " (" + path.clazz.getName() + ")");

            // The null check is really ugly, consider it temporary
            if(path.clazz!=null && path.clazz.isInstance(item)) {
            	result = path;
            	break;
            }
        }

        return result;
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

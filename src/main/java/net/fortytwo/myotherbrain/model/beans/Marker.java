package net.fortytwo.myotherbrain.model.beans;

import net.fortytwo.myotherbrain.model.MOB;
import org.openrdf.concepts.owl.Thing;
import org.openrdf.elmo.annotations.rdf;

/**
 * Author: josh
 * Date: May 7, 2009
 * Time: 7:45:54 PM
 */
@rdf(MOB.NAMESPACE + "Marker")
public interface Marker extends Thing {
}

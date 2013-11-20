package net.fortytwo.extendo.brain.server;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.rexster.RexsterResourceContext;
import com.tinkerpop.rexster.extension.ExtensionDefinition;
import com.tinkerpop.rexster.extension.ExtensionDescriptor;
import com.tinkerpop.rexster.extension.ExtensionNaming;
import com.tinkerpop.rexster.extension.ExtensionPoint;
import com.tinkerpop.rexster.extension.ExtensionRequestParameter;
import com.tinkerpop.rexster.extension.ExtensionResponse;
import com.tinkerpop.rexster.extension.RexsterContext;
import net.fortytwo.extendo.brain.Note;
import net.fortytwo.ripple.RippleException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A service for executing Ripple queries over Extend-o-Brain graphs
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
@ExtensionNaming(namespace = "extendo", name = "ripple")
//@ExtensionDescriptor(description = "execute a Ripple query over an Extend-o-Brain graph")
public class RippleExtension extends ExtendoExtension {

    @ExtensionDefinition(extensionPoint = ExtensionPoint.GRAPH)
    @ExtensionDescriptor(description = "an extension for performing Ripple queries over Extend-o-Brain graphs")
    public ExtensionResponse handleRequest(@RexsterContext RexsterResourceContext context,
                                           @RexsterContext Graph graph,
                                           @ExtensionRequestParameter(name = "query", description = "Ripple query") String query,
                                           @ExtensionRequestParameter(name = "depth", description = "depth of the view") Integer depth,
                                           @ExtensionRequestParameter(name = "minWeight", description = "minimum-weight criterion for atoms in the view") Float minWeight,
                                           @ExtensionRequestParameter(name = "maxWeight", description = "maximum-weight criterion for atoms in the view") Float maxWeight,
                                           @ExtensionRequestParameter(name = "minSharability", description = "minimum-sharability criterion for atoms in the view") Float minSharability,
                                           @ExtensionRequestParameter(name = "maxSharability", description = "maximum-sharability criterion for atoms in the view") Float maxSharability,
                                           @ExtensionRequestParameter(name = "style", description = "the style of view to generate") String styleName) {
        logInfo("extendo ripple \"" + query + "\"");

        Params p = createParams(context, (KeyIndexableGraph) graph);
        p.depth = depth;
        p.query = query;
        p.styleName = styleName;
        p.filter = createFilter(p.user, minWeight, maxWeight, -1, minSharability, maxSharability, -1);

        return handleRequestInternal(p);
    }

    protected ExtensionResponse performTransaction(final Params p) throws Exception {
        addSearchResults(p);

        p.map.put("title", p.query);
        return ExtensionResponse.ok(p.map);
    }

    protected boolean doesRead() {
        return true;
    }

    protected boolean doesWrite() {
        return false;
    }

    protected void addSearchResults(final Params p) throws IOException, RippleException {
        Note n = p.queries.rippleQuery(p.query, p.depth, p.filter, p.style);
        JSONObject json;

        try {
            json = p.writer.toJSON(n);
        } catch (JSONException e) {
            throw new IOException(e);
        }
        p.map.put("view", json.toString());
    }
}
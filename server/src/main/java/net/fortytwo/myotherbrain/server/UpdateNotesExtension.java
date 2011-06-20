package net.fortytwo.myotherbrain.server;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.rexster.RexsterResourceContext;
import com.tinkerpop.rexster.extension.AbstractRexsterExtension;
import com.tinkerpop.rexster.extension.ExtensionDefinition;
import com.tinkerpop.rexster.extension.ExtensionDescriptor;
import com.tinkerpop.rexster.extension.ExtensionNaming;
import com.tinkerpop.rexster.extension.ExtensionPoint;
import com.tinkerpop.rexster.extension.ExtensionRequestParameter;
import com.tinkerpop.rexster.extension.ExtensionResponse;
import com.tinkerpop.rexster.extension.HttpMethod;
import com.tinkerpop.rexster.extension.RexsterContext;
import net.fortytwo.myotherbrain.notes.Note;
import net.fortytwo.myotherbrain.notes.NotesIO;
import net.fortytwo.myotherbrain.notes.NotesViews;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: josh
 * Date: 6/19/11
 * Time: 1:40 PM
 */
@ExtensionNaming(namespace = "myotherbrain", name = "update-notes")
public class UpdateNotesExtension extends AbstractRexsterExtension {
    private static final Logger LOGGER = Logger.getLogger(UpdateNotesExtension.class.getName());

    @ExtensionDefinition(extensionPoint = ExtensionPoint.GRAPH, method = HttpMethod.POST)
    @ExtensionDescriptor(description = "an extension for updating a portion of a MyOtherBrain graph using the MOB Notes format")
    public ExtensionResponse handleUpdateRequest(@RexsterContext RexsterResourceContext context,
                                                 @RexsterContext Graph graph,
                                                 @ExtensionRequestParameter(name = "root", description = "root atom (vertex) of the view") String root,
                                                 @ExtensionRequestParameter(name = "view", description = "the updated view") String view) {
        //new Exception().printStackTrace(System.out);
        try {
            LOGGER.fine("update-notes request for: " + root);
            System.out.println("update-notes request for: " + root);
            int levels = 3;

            try {
                root = new Integer(root).toString();
            } catch (NumberFormatException e) {
                return ExtensionResponse.error("bad root id: " + root);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("root", root);
            map.put("levels", "" + levels);

            FramesManager manager = new FramesManager(graph);
            NotesViews m = new NotesViews(graph, manager);
            NotesIO p = new NotesIO();

            List<Note> update;

            InputStream in = new ByteArrayInputStream(view.getBytes());
            try {
                update = p.parseNotes(in);
            } finally {
                in.close();
            }

            // Apply the update
            m.applyUpdate(update, root, null, levels - 1);

            // Finally, generate a fresh view (post-update) and return it to the requester.
            Note n = m.toNote(root, null, levels);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                p.writeChildren(n, bos);
                map.put("view", bos.toString());
            } finally {
                bos.close();
            }

            return ExtensionResponse.ok(map);
        } catch (Exception e) {
            // TODO
            e.printStackTrace(System.out);
            return ExtensionResponse.error(e);
        }
    }
}
package net.fortytwo.smsn.brain.model.pg;

import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.error.InvalidGraphException;
import net.fortytwo.smsn.brain.model.Property;
import net.fortytwo.smsn.brain.model.Role;
import net.fortytwo.smsn.brain.model.entities.ListNode;
import net.fortytwo.smsn.brain.model.entities.Note;
import net.fortytwo.smsn.brain.model.entities.Topic;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PGNote extends PGEntity implements Note {

    private static final Map<String, Consumer<PGNote>> setterTriggersByPropertyKey;

    static {
        setterTriggersByPropertyKey = new HashMap<>();
        setterTriggersByPropertyKey.put(SemanticSynchrony.PropertyKeys.TITLE, PGNote::titleUpdated);
        setterTriggersByPropertyKey.put(SemanticSynchrony.PropertyKeys.SHORTCUT, PGNote::shortcutUpdated);
    }

    public PGNote(final Vertex vertex) {
        super(vertex);
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(final String id) {
        String atomId = null == id ? SemanticSynchrony.createRandomId() : id;
        setRequiredProperty(SemanticSynchrony.PropertyKeys.ID_V, atomId);

        getGraph().updateIndex(this, SemanticSynchrony.PropertyKeys.ID_V);
    }

    public Role getRole() {
        String name = getOptionalProperty(SemanticSynchrony.PropertyKeys.ROLE);
        return null == name ? null : Role.valueOf(name);
    }

    public void setRole(final Role role) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.ROLE, null == role ? null : role.name());
    }

    @Override
    public String getAlias() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.ALIAS);
    }

    @Override
    public void setAlias(String alias) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.ALIAS, alias);
    }

    @Override
    public Long getCreated() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.CREATED);
    }

    @Override
    public void setCreated(Long created) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.CREATED, created);
    }

    @Override
    public String getTitle() {
        return (String) getOptionalProperty(SemanticSynchrony.PropertyKeys.TITLE);
    }

    @Override
    public void setTitle(final String title) {
        setRequiredProperty(SemanticSynchrony.PropertyKeys.TITLE, title);
        titleUpdated();
    }

    @Override
    public String getText() {
        return (String) getOptionalProperty(SemanticSynchrony.PropertyKeys.TEXT);
    }

    @Override
    public void setText(String text) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.TEXT, text);
    }

    @Override
    public Float getPriority() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.PRIORITY);
    }

    @Override
    public void setPriority(Float priority) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.PRIORITY, priority);
    }

    @Override
    public String getShortcut() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.SHORTCUT);
    }

    @Override
    public void setShortcut(final String shortcut) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.SHORTCUT, shortcut);
        shortcutUpdated();
    }

    @Override
    public Float getWeight() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.WEIGHT);
    }

    @Override
    public void setWeight(Float weight) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.WEIGHT, weight);
    }

    @Override
    public String getSource() {
        return getOptionalProperty(SemanticSynchrony.PropertyKeys.SOURCE);
    }

    @Override
    public void setSource(final String source) {
        setOptionalProperty(SemanticSynchrony.PropertyKeys.SOURCE, source);
    }

    @Override
    public Topic getTopic() {
        return getExactlyOneEntity(SemanticSynchrony.EdgeLabels.TOPIC, Direction.OUT, v -> getGraph().asTopic(v));
    }

    @Override
    public void setTopic(final Topic topic) {
        setRequiredEntity(SemanticSynchrony.EdgeLabels.TOPIC, topic);
    }

    @Override
    public ListNode<Note> getChildren() {
        return getAtMostOneEntity(SemanticSynchrony.EdgeLabels.NOTES, Direction.OUT, v -> getGraph().asListOfAtoms(v));
    }

    @Override
    public void setChildren(ListNode<Note> children) {
        removeAllChildren();

        setChildrenInternal(children);
    }

    @Override
    public void forFirstOf(Consumer<ListNode<Note>> consumer) {
        forEachAdjacentVertex(SemanticSynchrony.EdgeLabels.FIRST, Direction.IN,
                vertex -> consumer.accept(getGraph().asListOfAtoms(vertex)));
    }

    @Override
    public void addChildAt(final Note child, int position) {
        // create a list node for the atom and insert it
        ListNode<Note> list = getGraph().createListOfNotes(child);
        if (0 == position) {
            list.setRest(getChildren());
            setChildrenInternal(list);
        } else {
            ListNode<Note> prev = getChildren();
            for (int i = 1; i < position; i++) {
                prev = prev.getRest();
            }

            list.setRest(prev.getRest());
            prev.setRest(list);
        }
    }

    @Override
    public void deleteChildAt(int position) {
        ListNode<Note> list = getChildren();

        // remove the atom's list node
        if (0 == position) {
            setChildrenInternal(list.getRest());

            deleteEntity(list);
        } else {
            ListNode<Note> prev = list;
            for (int i = 1; i < position; i++) {
                prev = prev.getRest();
            }

            ListNode<Note> l = prev.getRest();
            prev.setRest(l.getRest());
            deleteEntity(l);
        }
    }

    private void setChildrenInternal(ListNode<Note> children) {
        removeEdge(SemanticSynchrony.EdgeLabels.NOTES, Direction.OUT);

        if (null != children) {
            addOutEdge(((PGEntity) children).asVertex(), SemanticSynchrony.EdgeLabels.NOTES);
        }
    }

    private void removeAllChildren() {
        ListNode<Note> cur = getChildren();
        while (null != cur) {
            ListNode<Note> rest = cur.getRest();
            deleteEntity(cur);
            cur = rest;
        }
    }

    @Override
    public Collection<ListNode<Note>> getFirstOf() {
        List<ListNode<Note>> result = new java.util.LinkedList<>();
        forAllVertices(SemanticSynchrony.EdgeLabels.FIRST, Direction.IN,
                vertex -> result.add(getGraph().asListOfAtoms(vertex)));

        return result;
    }

    @Override
    public Note getSubject(ListNode<Note> notes) {
        PGEntity entity = (PGEntity) notes;
        return entity.getAtMostOneEntity(SemanticSynchrony.EdgeLabels.NOTES, Direction.IN,
                vertex -> getGraph().asAtom(vertex));
    }

    @Override
    public void destroy() {
        destroyInternal();
    }

    private void deleteEntity(final ListNode<Note> l) {
        ((PGEntity) l).asVertex().remove();
    }

    private void updateAcronym() {
        Vertex vertex = asVertex();
        String value = getTitle();
        String acronym = valueToAcronym(value);

        VertexProperty<String> previousProperty = vertex.property(SemanticSynchrony.PropertyKeys.ACRONYM);
        if (null != previousProperty) {
            previousProperty.remove();
        }

        if (null != acronym) {
            vertex.property(SemanticSynchrony.PropertyKeys.ACRONYM, acronym);
            getGraph().updateIndex(this, SemanticSynchrony.PropertyKeys.ACRONYM);
        }
    }

    private String valueToAcronym(final String value) {
        // index only short, name-like values, avoiding free-form text if possible
        if (null != value && value.length() <= 100) {
            String clean = cleanForAcronym(value);
            StringBuilder acronym = new StringBuilder();
            boolean isInside = false;
            for (byte b : clean.getBytes()) {
                // TODO: support international letter characters as such
                if (b >= 'a' && b <= 'z') {
                    if (!isInside) {
                        acronym.append((char) b);
                        isInside = true;
                    }
                } else if (' ' == b) {
                    isInside = false;
                }
            }

            return acronym.toString();
        } else {
            return null;
        }
    }

    private String cleanForAcronym(final String value) {
        return value.toLowerCase().replaceAll("[-_\t\n\r]", " ").trim();
    }

    private void titleUpdated() {
        getGraph().updateIndex(this, SemanticSynchrony.PropertyKeys.TITLE);
        updateAcronym();
    }

    private void shortcutUpdated() {
        getGraph().updateIndex(this, SemanticSynchrony.PropertyKeys.SHORTCUT);
    }

    private <V> V getProperty(final Property<Note, V> property) {
        V value = getOptionalProperty(property.getKey());
        if (null == value) {
            if (null != property.getDefaultValue()) {
                return property.getDefaultValue();
            } else if (property.isRequired()) {
                throw new InvalidGraphException("missing property '" + property.getKey() + "' for " + this);
            } else {
                return null;
            }
        } else {
            return value;
        }
    }

    private <V> void setProperty(final Property<Note, V> property, final V value) {
        V internalValue;
        if (null == value) {
            if (property.isRequired()) {
                throw new IllegalArgumentException("cannot set required property " + property.getKey() + " to null");
            } else {
                internalValue = null;
            }
        } else if (null != property.getDefaultValue() && property.getDefaultValue().equals(value)) {
            internalValue = null;
        } else {
            internalValue = value;
        }

        setOptionalProperty(property.getKey(), internalValue);
        Consumer<PGNote> trigger = setterTriggersByPropertyKey.get(property.getKey());
        if (null != trigger) {
            trigger.accept(this);
        }
    }
}
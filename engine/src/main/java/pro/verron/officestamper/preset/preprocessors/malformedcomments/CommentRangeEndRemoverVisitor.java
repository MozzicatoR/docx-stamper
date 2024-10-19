package pro.verron.officestamper.preset.preprocessors.malformedcomments;

import org.docx4j.utils.TraversalUtilVisitor;
import org.docx4j.wml.CommentRangeEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommentRangeEndRemoverVisitor
        extends TraversalUtilVisitor<CommentRangeEnd> {
    private static final Logger log = LoggerFactory.getLogger(CommentRangeEndRemoverVisitor.class);
    private final List<BigInteger> ids;
    private final Map<Object, List<Object>> toRemove = new HashMap<>();

    public CommentRangeEndRemoverVisitor(List<BigInteger> ids) {
        this.ids = ids;
    }

    @Override public void apply(CommentRangeEnd element, Object parent, List<Object> siblings) {
        if (ids.contains(element.getId())) toRemove.put(element, siblings);
    }

    public void run() {
        log.debug("Removed Comment Range Ends: {}", toRemove);
        toRemove.forEach((object, siblings) -> siblings.remove(object));
    }
}

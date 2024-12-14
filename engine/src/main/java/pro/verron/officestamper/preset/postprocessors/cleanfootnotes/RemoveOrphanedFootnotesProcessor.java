package pro.verron.officestamper.preset.postprocessors.cleanfootnotes;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import pro.verron.officestamper.api.PostProcessor;
import pro.verron.officestamper.preset.postprocessors.NoteRefsVisitor;
import pro.verron.officestamper.utils.WmlUtils;

import java.util.Collection;
import java.util.Optional;

import static org.docx4j.wml.STFtnEdn.NORMAL;
import static pro.verron.officestamper.api.OfficeStamperException.throwing;
import static pro.verron.officestamper.core.DocumentUtil.visitDocument;

public class RemoveOrphanedFootnotesProcessor
        implements PostProcessor {
    @Override
    public void process(WordprocessingMLPackage document) {
        var visitor = new NoteRefsVisitor();
        visitDocument(document, visitor);
        var referencedNoteIds = visitor.referencedNoteIds();
        var mainDocumentPart = document.getMainDocumentPart();

        var ftnPart = mainDocumentPart.getFootnotesPart();
        Optional.ofNullable(ftnPart)
                .stream()
                .map(throwing(FootnotesPart::getContents))
                .map(CTFootnotes::getFootnote)
                .flatMap(Collection::stream)
                .filter(RemoveOrphanedFootnotesProcessor::normalNotes)
                .filter(note -> !referencedNoteIds.contains(note.getId()))
                .toList()
                .forEach(WmlUtils::remove);
    }

    private static boolean normalNotes(CTFtnEdn note) {
        return Optional.ofNullable(note.getType())
                       .orElse(NORMAL)
                       .equals(NORMAL);
    }
}

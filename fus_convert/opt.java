package fus_convert;

import java.util.ArrayList;
import java.util.List;

public class opt {
    public KendraDocument setSubsection(TscsiEntry raw, KendraDocument document) {
    if (isValidStringList(document.getStringList(SUB_SECTION))) {
        return document;
    }

    String id = raw.getUrl();
    if (id == null) {
        return null;
    }

    // Cache the sections and limit the depth calculation to reduce redundant operations
    List<String> sections = symbolicPath(id);
    int maxDepth = limitDepth(sections, 2);

    List<String> parentsToAdd = new ArrayList<>();
    List<String> activePrefixes = new ArrayList<>();
    List<String> moreParents = new ArrayList<>();

    for (int i = 0; i < maxDepth; i++) {
        String section = sections.get(i);
        int adjustedDepth = adjustDepth(section, maxDepth);  // Cache adjusted depth
        clearPrefixes(section, activePrefixes);
        parentsToAdd.add(prefixed(section, activePrefixes));
        addPrefixes(section, activePrefixes);
        findExtraParents(section, moreParents);
    }

    parentsToAdd.addAll(moreParents);
    document.setStringList(SUB_SECTION, parentsToAdd);
    return document;
}

private List<String> symbolicPath(String url) {
    // Use substring once and reuse the result instead of recalculating
    String path = url.substring(PFX.length(), url.length() - 1);
    String[] arr = path.split("/");

    List<String> result = new ArrayList<>(arr.length);
    for (String part : arr) {
        result.add(withoutN(part).toLowerCase());
    }

    return result;
}


    
}
 
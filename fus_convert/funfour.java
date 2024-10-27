package fus_convert;

public class funfour {

    import java.util.*;

public class DocumentProcessor {

    private static final String PFX = "https://tscsi.jpmchase.net/gtts/";
    private static final String SECTION_F = "subsection_ss";

    private static final Map<String, String> ALL_ALIASES = new HashMap<>();
    private static final Map<String, String> ALL_PREFIXES = new HashMap<>() {{
        put("commercial_cards", "cc_");
        put("comm_card_imps", "cci_");
    }};
    private static final Set<String> NO_PREFIXES = new HashSet<>(Arrays.asList("trade_client_access"));
    private static final Map<String, List<String>> PARENT_LISTS = new HashMap<>() {{
        put("psc", Arrays.asList("payment_sol_center", "commercial_cards", "home_supp"));
    }};
    private static final Map<String, Integer> CUSTOM_DEPTHS = new HashMap<>() {{
        put("mer_ser_apac", 3);
    }};
    private static final Map<String, List<Object>> ADDED_PARENTS = new HashMap<>() {{
        put("access_supp", Arrays.asList("psc", 2));
        put("sc_supp", Arrays.asList("psc", 3));
        // Add other mappings as shown in the screenshot
    }};

    public Document processDocument(Document doc) {
        if (doc.hasField(SECTION_F)) return doc;

        String id = doc.getId();
        if (id == null) return null;

        List<String> activePrefixes = new ArrayList<>();
        List<String> moreParents = new ArrayList<>();

        List<String> sections = symbolicPath(id);
        int depth = limitDepth(sections, 2);

        for (int i = 0; i < depth; i++) {
            String section = aliased(sections.get(i));
            depth = limitDepth(sections, adjustDepth(section, depth));
            clearPrefixes(section, activePrefixes);
            doc.addField(SECTION_F, prefixed(section, activePrefixes));
            addPrefixes(section, activePrefixes);
            findExtraParents(section, moreParents);
        }

        for (String parent : moreParents) {
            doc.addField(SECTION_F, parent);
        }

        return doc;
    }

    private List<String> symbolicPath(String url) {
        String path = url.substring(PFX.length(), url.length() - 1);
        String[] arr = path.split("/");
        List<String> result = new ArrayList<>();
        for (String part : arr) {
            result.add(withoutN(part).toLowerCase());
        }
        return result;
    }

    private String withoutN(String str) {
        return (str != null && str.endsWith("_n")) ? str.substring(0, str.length() - 2) : str;
    }

    private String aliased(String key) {
        return ALL_ALIASES.getOrDefault(key, key);
    }

    private int adjustDepth(String section, int depth) {
        return CUSTOM_DEPTHS.getOrDefault(section, depth);
    }

    private void addPrefixes(String key, List<String> active) {
        String found = ALL_PREFIXES.get(key);
        if (found != null) {
            active.add(found);
        }
    }

    private void clearPrefixes(String key, List<String> active) {
        if (NO_PREFIXES.contains(key)) {
            active.clear();
        }
    }

    private String prefixed(String key, List<String> active) {
        StringBuilder join = new StringBuilder(key);
        for (String prefix : active) {
            join.insert(0, prefix);
        }
        return join.toString();
    }

    private void findExtraParents(String key, List<String> current) {
        List<Object> idxArr = ADDED_PARENTS.get(key);
        if (idxArr != null) {
            String listName = (String) idxArr.get(0);
            int len = (int) idxArr.get(1);
            List<String> list = PARENT_LISTS.get(listName).subList(0, len);
            for (String item : list) {
                if (!current.contains(item)) {
                    current.add(item);
                }
            }
        }
    }

    private int limitDepth(List<String> arr, int depth) {
        return Math.min(arr.size(), depth);
    }
}

    
}

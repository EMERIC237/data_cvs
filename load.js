function loadUrlParams() {
    console.log("Search Params: ", searchParams.toString());

    if (searchParams.get('query') !== null) {
        urlObject = deconstructUrlQuery(searchParams.get('query'));
    } else {
        urlObject = cleanUrlSearchParams(searchParams);
    }

    console.log("URL Object Before ADFS: ", urlObject);

    urlObject.lob = urlObject.lob || (props.adfsUser && props.adfsUser.lob);
    urlObject.taxonomy = urlObject.taxonomy || (props.adfsUser && props.adfsUser.taxonomy);
    urlObject.country = urlObject.country || (props.adfsUser && props.adfsUser.country);

    console.log("URL Object After ADFS: ", urlObject);

    setQueryText(urlObject.q);
    setId(urlObject.id || getId(urlObject.collection, ''));
    setCurrentFilters(urlObject.currentFilters);
    setQuerySite(urlObject.site);
    setQuerySort(urlObject.sort);
    setQueryStart(urlObject.start);
    setQueryCountry(urlObject.country);
    setQueryTaxonomy(urlObject.taxonomy);
    setQueryCollection(urlObject.collection || getCollection(urlObject, ''));
    setNeedsAPICall(true);
}

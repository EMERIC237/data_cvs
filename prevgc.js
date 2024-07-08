import React, { useState, useEffect, useRef } from 'react';
import gce3, { setAdfsUser } from './server/service/GCE3';
import { useLocation } from 'react-router-dom';

const SearchLayout = (props) => {
    const [needsAPICall, setNeedsAPICall] = useState(false);
    const [queryText, setQueryText] = useState('');
    const [queryCountry, setQueryCountry] = useState('');
    const [queryTaxonomy, setQueryTaxonomy] = useState('');
    const [queryCollection, setQueryCollection] = useState('');
    const location = useLocation();

    useEffect(() => {
        gce3.populateUser();
        loadUrlParams(new URLSearchParams(location.search));
    }, [location.search, props.adfsUser]);

    const loadUrlParams = (searchParams) => {
        let urlObject = {};

        if (searchParams.get('query') !== null) {
            urlObject = deconstructUrlQuery(searchParams.get('query'));
        } else {
            urlObject = cleanUrlSearchParams(searchParams);
        }

        urlObject.lob = urlObject.lob || (props.adfsUser && props.adfsUser.lob) || gce3.user.lob;
        urlObject.taxonomy = urlObject.taxonomy || (props.adfsUser && props.adfsUser.taxonomy) || gce3.user.taxonomy;
        urlObject.country = urlObject.country || (props.adfsUser && props.adfsUser.country) || gce3.user.country;

        setQueryText(urlObject.q);
        setQueryCountry(urlObject.country);
        setQueryTaxonomy(urlObject.taxonomy);
        setQueryCollection(urlObject.collection || getCollection(urlObject, ''));
        setNeedsAPICall(true);
    };

    useEffect(() => {
        if (needsAPICall) {
            callResultsAPI();
        }
    }, [needsAPICall]);

    const callResultsAPI = () => {
        const urlQueryInputs = {
            country: queryCountry,
            taxonomy: queryTaxonomy,
            // other query inputs
        };
        // Make API call with urlQueryInputs
    };

    return (
        <div className="container-fluid main-container">
            {/* Render your layout here */}
        </div>
    );
};

export default SearchLayout;

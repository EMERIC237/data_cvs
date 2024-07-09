let mode;

export const gce3 = {
    user: {},
    tabs: [],
    apptraytabs: [],
    errors: "GCE3 Debug",
    apptrayLOBdata: {},
    protocol: (document.location.protocol.toLowerCase().indexOf("https") >= 0) ? 'https://' : 'http://',
    mode: mode || "dev",
    recentSearches: [],
    BlockedExecutiveSIDs: [],
    ForeseeIncludeURLs: [],
    foreseeFileLoadCheck: 0
};

// Exported function to set ADFS user
export const setAdfsUser = (adfsUser) => {
    if (adfsUser && adfsUser.sid) {
        gce3.user = adfsUser;
    }
};

const getCookie = (strCookieField) => {
    // Implementation here
};

const getSessCookie = () => {
    let sGuid = "";
    const len = 10;
    const digits = new Uint8Array(len);

    for (let i = 0; i < len; i++) {
        sGuid += digits[i].toString(16);
    }

    return sGuid.substring(0, len);
};

const parseGceCountry = (rawCountry, gce3) => {
    // Implementation here
};

const parseGceState = (rawState, gce3) => {
    // Implementation here
};

const populatePersonalNames = (strFullName, gce3) => {
    // Implementation here
};

const parseCookie = () => {
    gce3.user = {
        country: '',
        countryName: '',
        sid: '',
        region: '',
        lob: '',
        city: '',
        sublob: '',
        fg: '',
        heritage: '',
        state: '',
        stateName: '',
        fullname: '',
        firstname: '',
        lastname: ''
    };

    gce3.sessCookie = gce3.getSessCookie("s_pens");
    let esourceStrCookie = '';

    const strCookie = gce3.getCookie("strCookieField");
    esourceStrCookie = gce3.getCookie("eSourceGate140");

    if (strCookie !== undefined && strCookie !== "" && strCookie !== "_notSet") {
        const arrCookie = strCookie.split("|");
        const strFullName = arrCookie[1].trim().replace(/\s+/g, " ");
        populatePersonalNames(strFullName, gce3);

        gce3.user.sid = arrCookie[2].trim();
        gce3.user.region = arrCookie[3].trim();
        gce3.user.country = gce3.getCookie("country");
        gce3.user.lob = gce3.getCookie("cts_Lob");
        gce3.user.taxonomy = gce3.getCookie("cts1_taxonomy");

        parseGceCountry(arrCookie[5], gce3);
        parseGceState(arrCookie[6], gce3);
        gce3.user.city = arrCookie[7].trim();
        gce3.user.sublob = arrCookie[5].trim();
        gce3.user.fg = "";
        gce3.user.heritage = "";
    } else if (esourceStrCookie !== "") {
        const arrCookie = esourceStrCookie.split("|");
        const strFullName = arrCookie[1].trim().replace(/\s+/g, " ");
        populatePersonalNames(strFullName, gce3);

        gce3.user.sid = arrCookie[2].trim();
        gce3.user.region = arrCookie[3].trim();
        gce3.user.lob = arrCookie[5].trim();
        gce3.user.city = arrCookie[7].trim();
        gce3.user.heritage = arrCookie[7].trim();
    }

    if (!gce3.user.sid) {
        gce3.user.fullname = "Guest";
        gce3.user.firstname = "Guest";
        gce3.user.lastname = "";
    }
};

// Factory function to initialize GCE3 with the given environment
export const GCE3 = (options) => {
    switch (options) {
        case 'development':
            mode = 'dev';
            break;
        case 'uat':
            mode = 'uat';
            break;
        case 'production':
            mode = 'prod';
            break;
        default:
            mode = 'dev';
            break;
    }

    gce3.user = {
        fullname: '',
        firstname: '',
        middleinitial: '',
        lastname: '',
        sid: '',
        region: '',
        country: '',
        state: '',
        city: '',
        sublob: '',
        heritage: '',
        fg: '',
        stateName: '',
        countryName: '',
        taxonomy: ''
    };

    gce3.populateUser = () => {
        if (!gce3.user.sid) parseCookie();
    };

    gce3.getCookie = getCookie;
    gce3.getSessCookie = getSessCookie;
    gce3.parseCookie = parseCookie;
    gce3.populateUser();

    return gce3;
};

export default gce3;

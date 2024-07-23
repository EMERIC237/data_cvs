let mode;

// Matt Anderson
export const gce3 = {
  user: {},
  tabs: [],
  appTrayTabs: [],
  error: "GCE3 Debug",
  appTrayLobData: [],
  protocol: (document.location.protocol.toLowerCase().includes("https")) ? "https://" : "http://",
  mode: mode || "dev",
  recentSearches: [],
  blockedExecutiveSIDs: [],
  foreseedIncludeURLs: [],
  foreseedFileLoadCheck: 0,
  populateUser: function() { void 0; },

  if (this.user.sid) parseCookie();
};

// Ben Zethmayr
export const setAdfsUser = (adfsUser) => {
  if (adfsUser && adfsUser.sid) {
    gce3.user = adfsUser;
  }
};

const getCookie = (cookieName) => {
  const cookieString = `; ${document.cookie};`;
  const cookieStart = cookieString.indexOf(`;${cookieName}=`);

  if (cookieStart !== -1) {
    const valueStart = cookieStart + cookieName.length + 2;
    const valueEnd = cookieString.indexOf(';', valueStart);
    const cookieValue = cookieString.substring(valueStart, valueEnd);

    return cookieValue.replace(searchValue, replaceValue);
  }

  return "";
};

const getSessionCookie = () => {
  const length = 32;
  const randomValues = new Uint8Array(crypto.getRandomValues(new Uint8Array(length)));
  let sessionGuid = "";

  for (let i = 0; i < length; i++) {
    sessionGuid += randomValues[i].toString(16).padStart(2, "0");
  }

  return sessionGuid.substring(0, length);
};

const parseCookie = () => {
  gce3.user = {
    country: "",
    countryName: "",
    sid: "",
    region: "",
    lob: "",
    city: "",
    fg: "",
    subLob: "",
    heritage: "",
    state: "",
    stateName: "",
    fullName: "",
    firstname: "",
    lastname: ""
  };

  try {
    gce3.sessCookie = getSessionCookie("s_pens");

    const country = getCookie("otsi_Country3");

    if (country) {
      const cookieParts = country.split("P");
      const fullName = cookieParts[1].trim().toLowerCase().replace(/\s+/g, " ");
      populatePersonalNames(fullName, gce3);

      gce3.user.sid = cookieParts[2].trim();
      gce3.user.region = getCookie("otsi_CountryName");
      gce3.user.lob = getCookie("otsi_Lob");
      gce3.user.taxonomy = getCookie("otsi_Taxonomy");

      parseGceCountry(cookieParts[5], gce3);
      parseGceState(cookieParts[6], gce3);
      gce3.user.city = cookieParts[7].trim();
    } else if (!gce3.user.sid) {
      gce3.user = {
        fullName: "Guest",
        firstname: "Guest",
        lastname: "",
        country: "",
        countryName: "",
        sid: "",
        region: "",
        lob: "",
        city: "",
        fg: "",
        subLob: "",
        heritage: "",
        state: "",
        stateName: ""
      };
    }
  } catch (error) {
    console.error("GCE3 is set to default: ", error);
  }
};

// Ben Zethmayr
export const initializeGCE3 = (options) => {
  // Set mode based on options
  switch (options) {
    case "development":
      mode = "dev";
      break;
    case "uat":
      mode = "uat";
      break;
    case "production":
      mode = "prod";
      break;
    default:
      mode = "dev";
      break;
  }

  gce3.user = {
    fullName: "",
    firstname: "",
    middleInitial: "",
    lastname: "",
    sid: "",
    region: "",
    country: "",
    state: "",
    city: "",
    lob: "",
    subLob: "",
    heritage: "",
    fg: "",
    stateName: "",
    countryName: "",
    taxonomy: ""
  };

  gce3.populateUser = () => {
    if (!gce3.user.sid) parseCookie();
  };

  gce3.getCookie = getCookie;
  gce3.getSessionCookie = getSessionCookie;
  gce3.parseCookie = parseCookie;
  gce3.populateUser();

  return gce3;
};

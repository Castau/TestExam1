//Change this before deployment (production)
import { localURL as URL} from "./settings.js";
function handleHttpErrors(res) {
  if (!res.ok) {
    return Promise.reject({ status: res.status, fullError: res.json() });
  }
  return res.json();
}

const ApiFacade = () => {
  //Insert utility-methods from a latter step (d) here
  function makeOptions(method, addToken, body) {
    var opts = {
      method: method,
      headers: {
        "Content-type": "application/json",
        Accept: "application/json"
      }
    };
    if (addToken && loggedIn()) {
      opts.headers["x-access-token"] = getToken();
    }
    if (body) {
      opts.body = JSON.stringify(body);
    }
    return opts;
  }

  function setToken(token) {
    localStorage.setItem("jwtToken", token);
  }

  function getToken() {
    return localStorage.getItem("jwtToken");
  }

  function loggedIn() {
    const loggedIn = getToken() != null;
    return loggedIn;
  }
  
  const fetchSwapi = () => {
    //Remember to always include options from the makeOptions fucntion with >true< as the second parameter
    //if you want to access a protected endpoint
    const options = makeOptions("GET",true); //True add's the token
    return fetch(URL + "/api/info/five", options).then(handleHttpErrors);
  }

  //Roles is passed in as parameter from the LoggedIn component in App.js
  const fetchData = (roles) => {
    //In order to use the correct endpoints we have to check the roles of the user
    let usertype = "no role";
    //Currently we have three endpoints: "user", "admin" and "both"
    //so we just check the roles array to see the roles of the logged in user
    //Check line 54-55 in LoginEndpoint.java (backend)
    if (roles.includes("user") && roles.includes("admin")) usertype = "both";
    else if (roles.includes("user")) usertype = "user";
    else if (roles.includes("admin")) usertype = "admin";
    const options = makeOptions("GET",true); //True add's the token
    //The usertype is added to the URL to ensure the right endpoint is used
    return fetch(URL + "/api/info/" + usertype, options).then(handleHttpErrors);
  }

  const login = (user, pass, setRoles) => {
    const options = makeOptions("POST", true, {
      username: user,
      password: pass
    });
    return fetch(URL + "/api/login", options)
      .then(handleHttpErrors)
      .then(res => {
        setToken(res.token);
        setRoles(res.roles);
      });
  };

  const logout = () => {
    localStorage.removeItem("jwtToken");
  };

  return {
    login,
    logout,
    fetchData,
    fetchSwapi
  };
};

let returnVal = ApiFacade();
export default returnVal;

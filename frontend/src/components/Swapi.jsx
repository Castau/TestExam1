import React, { useState, useEffect } from "react";
import facade from "../apiFacade";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link } from "react-router-dom";

const Swapi = ({ loggedIn }) => {
  return (
    <>
      {loggedIn ? <SwapiContent /> : <p>You are not logged in.</p>}

      <Link to="/">Back</Link>
    </>
  );
};

const SwapiContent = () => {
  // MAKE CALL HERE TO APIFACADE HERE AND SHOW IT
  const [data, setData] = useState("");

  useEffect(() => {
    facade
      .fetchSwapi()
      .then(res => setData(res))
      .catch(err => {
        if (err.status) {
          err.fullError.then(e => console.log(e.code, e.message));
        } else {
          console.log("Network error");
        }
      });
  }, []);
  return (
    <>
      <p>Logged in and on Swapi</p>
      <br></br>
      <p>Data fetched from backend:</p>
      <p>{JSON.stringify(data)}</p>
    </>
  );
};

export default Swapi;

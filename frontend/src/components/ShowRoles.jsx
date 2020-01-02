import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const Show = ({ roles }) => {
    let prettyRoles = roles;
    prettyRoles = prettyRoles.replace(/\[|\]|\"/g, '').replace(',', ' & ').toUpperCase();

    return (
        <p>Permission: {prettyRoles}</p>
    );
};

export default Show;
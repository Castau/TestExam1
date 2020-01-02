import React, {useState, useEffect} from 'react';
import facade from './apiFacade';
import 'bootstrap/dist/css/bootstrap.min.css';
import {LogIn, LoggedIn} from './components/Login.jsx';
import WelcomePage from './components/Welcome.jsx';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    NavLink,
    useParams,
    useRouteMatch,
    Link,
    Prompt
} from "react-router-dom";
import Swapi from "./components/Swapi.jsx";
import ShowRoles from "./components/ShowRoles.jsx";

const App = () => {
	const [loggedIn, setLoggedIn] = useState(false);
	return (
		<>
			<Router>
				<Header loggedIn={loggedIn} />
				<div className="container">
					<Switch>
						<Route exact path="/">
							<WelcomePage />
						</Route>
						<Route path="/login">
							<LogInScreen permission={setLoggedIn} />
						</Route>
						<Route path="/swapi">
							<Swapi loggedIn={loggedIn} />
						</Route>
						<Route>
							<NoMatch />
						</Route>
					</Switch>
				</div>
			</Router>
		</>
	);
};

const NoMatch = () => (
	<div>You're trying to access a resource that doesn't exist.</div>
);

const Header = ({loggedIn}) => {
	return (
		<ul className="header">
			<li>
				<NavLink exact activeClassName="active" to="/">
					Home
				</NavLink>
			</li>
			<li>
				<NavLink activeClassName="active" to="/login">
					Login
				</NavLink>
			</li>
			{loggedIn ? (
				<li>
					<NavLink activeClassName="active" to="/swapi">
						Star Wars API
					</NavLink>
				</li>
			) : null}
		</ul>
	);
};

const LogInScreen = ({permission}) => {
	const [loggedIn, setLoggedIn] = useState(false);
	const [message, setMessage] = useState('');
	const [roles, setRoles] = useState([]);
	const logout = () => {
		facade.logout();
		setLoggedIn(false);
	};
	const login = (user, pass) => {
		facade
			.login(user, pass, setRoles)
			.then(res => {
				setMessage('');
				setLoggedIn(true);
			})
			.catch(err => {
				if (err.status) {
					setMessage('Failed to log in, check your information');
					err.fullError.then(e => console.log(e.code, e.message));
				} else {
					console.log('Network error');
				}
			});
	};
    // Lifts up the state of loggedIn so that we can make routes private.
    useEffect(() => {
        permission(loggedIn);
    }, [loggedIn]);

    return (
        <div>
            {!loggedIn ? (<LogIn login={login} message={message} />) : (
                <div>
                    <ShowRoles roles={roles} />
                    <LoggedIn roles={roles} />
                    <button className="btn btn-primary" onClick={logout}> Logout  </button>
                </div>)}
            <br></br>
            <Link to="/">Back to WelcomePage</Link>
        </div>
    );
};

export default App;

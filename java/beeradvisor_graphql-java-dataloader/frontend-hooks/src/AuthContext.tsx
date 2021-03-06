import * as React from "react";
import { withApollo } from "@apollo/react-hoc";
import { ApolloClient } from "apollo-client";
import gql from "graphql-tag";

interface AuthInfoState {
  auth: {
    userId: string;
    username: string;
  };
}

interface AuthErrorState {
  error: string;
}

interface IAuthContext {
  auth: AuthInfoState | AuthErrorState | null;
  login(userId: string): void;
}

const AuthContext = React.createContext<IAuthContext>({
  auth: null,
  login() {}
});

const getAuthToken = () => sessionStorage.getItem("auth-token");
const setAuthToken = (authToken: string | null) =>
  authToken ? sessionStorage.setItem("auth-token", authToken) : sessionStorage.removeItem("auth-token");

interface AuthProviderProps {
  client: ApolloClient<any>;
}

interface AuthProviderState {
  auth: AuthInfoState | AuthErrorState | null;
}

const LoginMutation = gql`
  mutation LoginMutation($username: String!) {
    login(username: $username) {
      authentication {
        userId
        username
        authToken
      }
      error
    }
  }
`;

class AuthProvider extends React.Component<AuthProviderProps, AuthProviderState> {
  readonly state: AuthProviderState = {
    auth: null
  };

  login = async (loginId: string) => {
    const { client } = this.props;
    setAuthToken(null);

    const loginResult = await client.mutate({
      mutation: LoginMutation,
      variables: {
        username: loginId
      }
    });

    if (!loginResult.data) {
      this.setState({
        auth: {
          error: `Could not authenticate (${loginResult.errors})`
        }
      });
      return;
    }

    const { error, authentication } = loginResult.data.login;

    if (error) {
      this.setState({
        auth: {
          error: `Could not authenticate (${error})`
        }
      });
      return;
    }

    setAuthToken(authentication.authToken);

    this.setState({
      auth: {
        auth: {
          userId: authentication.userId,
          username: authentication.username
        }
      }
    });
  };

  render() {
    return (
      <AuthContext.Provider
        value={{
          auth: this.state.auth,
          login: this.login
        }}
      >
        {this.props.children}
      </AuthContext.Provider>
    );
  }
}

const AuthProviderWithGraphQL = withApollo<{}>(AuthProvider);

function useAuthContext() {
  const authContext = React.useContext(AuthContext);
  return authContext;
}

export { AuthProviderWithGraphQL as AuthProvider, useAuthContext, setAuthToken, getAuthToken };

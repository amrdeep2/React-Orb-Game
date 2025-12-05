/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cst8218.chab0109.orbgame.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.annotation.security.DeclareRoles;

@BasicAuthenticationMechanismDefinition
@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "java:app/mariaDB",
    callerQuery = "SELECT PASSWORD FROM users WHERE USERID = ?",
    groupsQuery = "SELECT ROLENAME FROM users WHERE USERID = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class
)
@DeclareRoles({"JSFGroup", "APIGroup", "AdmGroup"})
@ApplicationScoped
public class SecurityConfig {

}

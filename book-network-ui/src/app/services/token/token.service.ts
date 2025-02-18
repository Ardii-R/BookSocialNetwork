import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  constructor() { }

  set token(token:string) {
    localStorage.setItem('token', token);
  }

  get token(): string {
    return localStorage.getItem('token') as string;
  }

  isTokenNotValid() {
    return !this.isTokenValid();
  }

  isTokenValid() {
    const token = this.token; 
    if(!token) {
      return false;
    }
    // decode token
    const jwthelper = new JwtHelperService();
    // check expiration
    const isTokenExpired = jwthelper.isTokenExpired(token);

    if(isTokenExpired){
      // remove token from local storage
      localStorage.removeItem('token');
      return false;
    }
    return true;
  }

}

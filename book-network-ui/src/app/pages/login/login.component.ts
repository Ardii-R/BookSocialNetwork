import { Component } from '@angular/core';
import { AuthenticationRequest } from '../../services/models';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';
import { TokenService } from '../../services/token/token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {


  // hold the credentials
  authRequest: AuthenticationRequest = {
    email: '',
    password: ''
  }

  // holds the errors returned from the backend
  errorMsg: Array<string> = [];


  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private tokenService: TokenService
  ){}


  login() {
    this.errorMsg = [];
    this.authenticationService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        // save the token
        this.tokenService.token = res.token as string;
        // navigate page
        this.router.navigate(['books']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    });
  }

    register() {
      this.router.navigate(["register"]);
    }


}



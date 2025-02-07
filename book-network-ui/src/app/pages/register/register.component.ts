import { Component } from '@angular/core';
import { RegistrationRequest } from '../../services/models';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  // holds the errors returned from the backend
  errorMsg: Array<string> = [];
  // holds the registration request from the form
  registerRequest: RegistrationRequest = {
    email: '',
    firstname: '',
    lastname: '',
    password: ''
  }

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
  ){}


  register(): void {
    this.errorMsg = [];

    this.authenticationService.register({
      body: this.registerRequest
    }).subscribe({
      next: (res) => {
        // if the registration is successful then redirect user to activate his account
        this.router.navigate(['activate-account']);
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors;
      }
    }
    );

  }


  signin():void {
    this.router.navigate(['/login']);
  }
  



}

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/services';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {

  message: string ="";
  isOkay: boolean = true;
  submitted: boolean = false;


  constructor(
    private route: Router,
    private authenticationService: AuthenticationService,
  ){ }


  redirectToLogin(): void {
    this.route.navigate(['login']);
  }

  onCodeCompleted(token: string) {

    this.confirmedAccount(token);

  }
  confirmedAccount(token: string) {
    this.authenticationService.confirmAccount({
      token: token
    }).subscribe({
      next: () => {
        this.message = "Account confirmed successfully!";
        this.isOkay = true;
        this.submitted = true;
      },
      error: (error) => {
        this.message = error.error.errorMsg;
        this.submitted = true;
        this.isOkay = false;
      }
    });
  }
    

}

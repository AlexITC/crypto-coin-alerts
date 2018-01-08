import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home/home.component';
import { NewAccountComponent } from './new-account/new-account.component';
import { LoginComponent } from './login/login.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';
import { NewFixedPriceAlertComponent } from './new-fixed-price-alert/new-fixed-price-alert.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'new-account', component: NewAccountComponent },
  { path: 'login', component: LoginComponent },
  { path: 'verify-email/:token', component: VerifyEmailComponent },
  { path: 'new-fixed-price-alert', component: NewFixedPriceAlertComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {

}

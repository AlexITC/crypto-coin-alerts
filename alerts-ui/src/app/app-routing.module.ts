import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home/home.component';
import { NewAccountComponent } from './new-account/new-account.component';
import { LoginComponent } from './login/login.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';
import { FixedPriceAlertsComponent } from './fixed-price-alerts/fixed-price-alerts.component';
import { NewFixedPriceAlertComponent } from './new-fixed-price-alert/new-fixed-price-alert.component';
import { NewCurrencyAlertsComponent } from './new-currency-alerts/new-currency-alerts.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'new-account', component: NewAccountComponent },
  { path: 'login', component: LoginComponent },
  { path: 'verify-email/:token', component: VerifyEmailComponent },
  { path: 'fixed-price-alerts', component: FixedPriceAlertsComponent },
  { path: 'new-fixed-price-alert', component: NewFixedPriceAlertComponent },
  { path: 'new-currency-alerts', component: NewCurrencyAlertsComponent },
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

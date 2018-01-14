import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AlertModule, BsDropdownModule, TooltipModule, ModalModule } from 'ngx-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { ToastrModule } from 'ngx-toastr';

import { NgHttpLoaderModule } from 'ng-http-loader/ng-http-loader.module'

import { NgxPaginationModule } from 'ngx-pagination';

import { AppComponent } from './app.component';
import { AppNavbarComponent } from './app-navbar/app-navbar.component';
import { NewAccountComponent } from './new-account/new-account.component';
import { LoginComponent } from './login/login.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

import { UsersService } from './users.service';
import { FixedPriceAlertsService } from './fixed-price-alerts.service';
import { ExchangeCurrencyService } from './exchange-currency.service';
import { ErrorService } from './error.service';
import { AuthService } from './auth.service';
import { ReCaptchaService } from './re-captcha.service';
import { NotificationService } from './notification.service';

import { AppRoutingModule } from './app-routing.module';
import { JWTInterceptor } from './jwt.interceptor';
import { NewFixedPriceAlertComponent } from './new-fixed-price-alert/new-fixed-price-alert.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';
import { FixedPriceAlertsComponent } from './fixed-price-alerts/fixed-price-alerts.component';

@NgModule({
  declarations: [
    AppComponent,
    AppNavbarComponent,
    NewAccountComponent,
    LoginComponent,
    VerifyEmailComponent,
    NewFixedPriceAlertComponent,
    FooterComponent,
    HomeComponent,
    FixedPriceAlertsComponent
  ],
  imports: [
    AppRoutingModule,
    AlertModule.forRoot(),
    BsDropdownModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    CommonModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgHttpLoaderModule,
    TranslateModule.forRoot(),
    NgxPaginationModule
  ],
  providers: [
    UsersService,
    FixedPriceAlertsService,
    ExchangeCurrencyService,
    AuthService,
    ErrorService,
    ReCaptchaService,
    NotificationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JWTInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

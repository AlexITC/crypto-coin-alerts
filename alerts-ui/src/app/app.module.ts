import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AlertModule, BsDropdownModule, CollapseModule, TooltipModule, ModalModule } from 'ngx-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import { ToastrModule } from 'ngx-toastr';

import { NgHttpLoaderModule } from 'ng-http-loader/ng-http-loader.module'

import { NgxPaginationModule } from 'ngx-pagination';

import { ReCaptchaModule } from 'angular2-recaptcha';

import { AppComponent } from './app.component';
import { AppNavbarComponent } from './app-navbar/app-navbar.component';
import { NewAccountComponent } from './new-account/new-account.component';
import { LoginComponent } from './login/login.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

import { UsersService } from './users.service';
import { FixedPriceAlertsService } from './fixed-price-alerts.service';
import { ExchangeCurrencyService } from './exchange-currency.service';
import { NewCurrencyAlertsService } from './new-currency-alerts.service';
import { ErrorService } from './error.service';
import { AuthService } from './auth.service';
import { ReCaptchaService } from './re-captcha.service';
import { NotificationService } from './notification.service';
import { NavigatorService } from './navigator.service';

import { AppRoutingModule } from './app-routing.module';
import { JWTInterceptor } from './jwt.interceptor';
import { LangInterceptor } from './lang.interceptor';
import { NewFixedPriceAlertComponent } from './new-fixed-price-alert/new-fixed-price-alert.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';
import { FixedPriceAlertsComponent } from './fixed-price-alerts/fixed-price-alerts.component';
import { NewCurrencyAlertsComponent } from './new-currency-alerts/new-currency-alerts.component';
import { LanguageService } from './language.service';

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
    FixedPriceAlertsComponent,
    NewCurrencyAlertsComponent
  ],
  imports: [
    AppRoutingModule,
    AlertModule.forRoot(),
    BsDropdownModule.forRoot(),
    CollapseModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    CommonModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgHttpLoaderModule,
    TranslateModule.forRoot(),
    NgxPaginationModule,
    ReCaptchaModule
  ],
  providers: [
    UsersService,
    FixedPriceAlertsService,
    ExchangeCurrencyService,
    NewCurrencyAlertsService,
    AuthService,
    ErrorService,
    ReCaptchaService,
    NotificationService,
    NavigatorService,
    LanguageService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JWTInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LangInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

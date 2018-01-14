import { Injectable } from '@angular/core';

import { Router } from '@angular/router';

@Injectable()
export class NavigatorService {

  constructor(private router: Router) { }

  go(path: string) {
    this.router.navigate([path]);
  }

  home() {
    this.go('/');
  }

}

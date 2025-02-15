import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrl: './rating.component.scss'
})
export class RatingComponent {

  @Input()
  rating: number = 0;
  maxRating: number = 5;


  get fullStars(): number {
    return Math.floor(this.rating);
  }

  get hasHalfStar(): boolean {
    // check if half reating with modulo 
    return this.rating % 1 !== 0;
  }

  get emptyStars(): number {
    // calculate how many empty stars are needed
    return this.maxRating - Math.ceil(this.rating);
  }



}

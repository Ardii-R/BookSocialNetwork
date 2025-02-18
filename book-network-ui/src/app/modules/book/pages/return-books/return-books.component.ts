import { Component, OnInit } from '@angular/core';
import { BookService } from '../../../../services/services/book.service';
import { BorrowedBookResponse, PageResponseBorrowedBookResponse } from '../../../../services/models';

@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrl: './return-books.component.scss'
})
export class ReturnBooksComponent implements OnInit {

  page = 0;
  size = 7; // paging size 
  pages: any = [];
  returnedBooks: PageResponseBorrowedBookResponse= {};
  borrowMessage = '';
  level: 'error'| 'success' = 'success';


  constructor(private bookSerivce: BookService){}

  ngOnInit(): void {
    this.findAllReturnedBooks();
  }

  findAllReturnedBooks() {
    this.bookSerivce.findAllReturnedBooks(
      {
        page: this.page,
        size: this.size,
        connectedUser: "ANY"
      }
    ).subscribe({
      next: (response) => {
        this.returnedBooks = response;
      }
    });
  }

    
  // ***** pagination *****
  goToLastPage() {
    this.page = this.returnedBooks.totalPages as number -1;
    this.findAllReturnedBooks();
  }
  goToNextPage() {
    this.page++;
    this.findAllReturnedBooks();
  }
  goToPreviousPage() {
    this.page--;
    this.findAllReturnedBooks();
  }
  goToFirstPage() {
    this.page = 0;
    this.findAllReturnedBooks();
  }
  goToPage(page: number) {
    this.page = page;
    this.findAllReturnedBooks();
  }

  get isLastPage() {
    return this.page === this.returnedBooks.totalPages as number - 1;
  }


  approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned) {
      return;
    }
    this.bookSerivce.approveReturnBorrowBook({
      'book-id': book.id as number,
      connectedUser: "ANY"
    }).subscribe({
      next: () => {
        this.level = "success"
        this.borrowMessage = "Book returned approved successfully!";
        this.findAllReturnedBooks();
      }
    });
  }
}

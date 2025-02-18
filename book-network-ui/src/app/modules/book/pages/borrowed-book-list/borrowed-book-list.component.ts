import { Component, OnInit } from '@angular/core';
import { BookResponse, BorrowedBookResponse, FeedbackRequest, PageResponseBorrowedBookResponse } from '../../../../services/models';
import { BookService, FeedbackService } from '../../../../services/services';

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrl: './borrowed-book-list.component.scss'
})
export class BorrowedBookListComponent implements OnInit {

  page = 0;
  size = 7; // paging size 
  pages: any = [];
  selectedBook: BookResponse | undefined = undefined;
  borrowedBooks: PageResponseBorrowedBookResponse = {};
  feedbackRequest: FeedbackRequest = {bookId: 0, comment: '', note: 0};


  constructor(private bookSerivce: BookService, private feedbackService: FeedbackService ) { }

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }
  
  findAllBorrowedBooks() {
    this.bookSerivce.findAllBorrowedBooks(
      {
        page: this.page,
        size: this.size,
        connectedUser: "ANY"
      }
    ).subscribe({
      next: (response) => {
        this.borrowedBooks = response;
      }
    });
  }


    // ***** pagination *****

    goToLastPage() {
      this.page = this.borrowedBooks.totalPages as number -1;
      this.findAllBorrowedBooks();
    }
    goToNextPage() {
      this.page++;
      this.findAllBorrowedBooks();
    }
    goToPreviousPage() {
      this.page--;
      this.findAllBorrowedBooks();
    }
    goToFirstPage() {
      this.page = 0;
      this.findAllBorrowedBooks();
    }
    goToPage(page: number) {
      this.page = page;
      this.findAllBorrowedBooks();
    }
  
    get isLastPage() {
      return this.page === this.borrowedBooks.totalPages as number - 1;
    }

    // ***** functionality *****

    returnBorrowedBook(book: BorrowedBookResponse) {
      this.selectedBook = book
      this.feedbackRequest.bookId = book.id as number;
    }

    returnBook(withFeedBack: boolean) {

      console.log("id test: " + this.selectedBook?.id)

      this.bookSerivce.returnBorrowBook(
        {
          "book-id": this.selectedBook?.id as number,
          connectedUser: 'ANY' // Stelle sicher, dass es eine gültige Enum-Option ist
        }).subscribe( {
        next: () => {
          if(withFeedBack){
            this.giveFeedback();
          }
          this.selectedBook = undefined;
          this.findAllBorrowedBooks();
        }
      })
    }

  giveFeedback() {
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest,
      connectedUser: "ANY"
    }).subscribe({
      next: () => {

      }
    });
  }

}
